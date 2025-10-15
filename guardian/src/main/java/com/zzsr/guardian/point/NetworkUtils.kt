// guardian/src/main/java/com/zzsr/guardian/point/NetworkUtils.kt

package com.zzsr.guardian.point

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.zzsr.guardian.GuardianSdk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 全局网络请求工具类。
 * - 负责构建包含通用参数的网络请求。
 * - 提供协程和回调两种异步请求方式。
 * - 实现了设备ID的本地持久化和内存缓存。
 * - 所有外部配置（如渠道ID）都通过 GuardianSdk 获取。
 */
object NetworkUtils {

    // 创建 OkHttpClient 实例，可以添加日志拦截器便于调试
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            // 在 debug 版本中打印详细日志
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private lateinit var appContext: Context

    // 用于设备ID持久化的 SharedPreferences 相关常量和内存缓存变量
    private const val PREFS_NAME = "guardian_sdk_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    @Volatile // 确保多线程可见性
    private var cachedDeviceId: String? = null

    /**
     * [必需] 初始化 NetworkUtils。此方法由 GuardianSdk.init() 自动调用。
     * @param context 必须是 ApplicationContext，以防止内存泄漏。
     */
    internal fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * 网络请求回调接口，用于 Java 或传统 Kotlin 调用。
     */
    interface NetworkCallback {
        fun onSuccess(response: String)
        fun onFailure(e: Exception)
    }

    /**
     * 【协程方式】执行 POST 请求。
     * @param url 请求的URL
     * @param token [可选] 用户登录的token
     * @param locationPoint [可选] 打点位置的详细信息 (JSON 字符串)
     * @param pointType 打点类型 (如 "click")
     * @return 响应体字符串
     * @throws IOException 如果网络请求失败
     */
    @JvmStatic
    suspend fun postRequestAsync(
        url: String,
        token: String? = null,
        locationPoint: String? = null,
        pointType: String
    ): String = withContext(Dispatchers.IO) {
        val formBody = buildBaseFormBody(token, locationPoint, pointType).build()
        val request = Request.Builder().url(url).post(formBody).build()

        val response = okHttpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Request failed with code: ${response.code}, message: ${response.message}")
        }
        response.body?.string() ?: ""
    }

    /**
     * 【回调方式】执行 POST 请求。
     * @param url 请求的URL
     * @param token [可选] 用户登录的token
     * @param locationPoint [可选] 打点位置的详细信息 (JSON 字符串)
     * @param pointType 打点类型 (如 "click")
     * @param callback 回调接口
     */
    @JvmStatic
    fun postRequestWithCallback(
        url: String,
        token: String? = null,
        locationPoint: String?,
        pointType: String,
        callback: NetworkCallback?
    ) {
        try {
            val formBody = buildBaseFormBody(token, locationPoint, pointType).build()
            val request = Request.Builder().url(url).post(formBody).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback?.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val responseBody = response.body?.string() ?: ""
                            callback?.onSuccess(responseBody)
                        } catch (e: Exception) {
                            callback?.onFailure(e)
                        }
                    } else {
                        val error = IOException("Request failed with code: ${response.code}, message: ${response.message}")
                        callback?.onFailure(error)
                    }
                }
            })
        } catch (e: Exception) {
            callback?.onFailure(e)
        }
    }

    /**
     * 构建包含所有通用参数的 FormBody.Builder。
     */
    private fun buildBaseFormBody(
        token: String?,
        locationPoint: String?,
        pointType: String
    ): FormBody.Builder {
        val builder = FormBody.Builder()

        // 1. Token (从参数获取)
        token?.takeIf { it.isNotBlank() }?.let { builder.add("utoken", it) }

        // 2. App 包名和版本号
        builder.add("app_package", appContext.packageName)
        try {
            val pInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            // 通过 GuardianSdk 获取解耦后的 channelId
            val channelId = GuardianSdk.getChannelId()
            builder.add("version", "${pInfo.versionCode},$channelId")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            builder.add("version", "unknown,${GuardianSdk.getChannelId()}")
        }

        // 3. 设备信息
        builder.add("device_id", getDeviceId())
        builder.add("device_model", "${Build.MANUFACTURER},${Build.MODEL}")

        // 4. 环境检测 (这些工具类也应该在库内部)
        val isVirtual = VirtualDeviceDetector.isVirtualDevice(appContext)
        builder.add("is_virtual_machine", if (isVirtual) "1" else "0")
        builder.add("is_proxy_network", ProxyDetector.isProxyNetwork(appContext))

        // 5. 经纬度
        if (hasLocationPermission()) {
            getLastLocation()?.let { (latitude, longitude) ->
                builder.add("latitude", latitude.toString())
                builder.add("longitude", longitude.toString())
            }
        }

        // 6. 具体的打点信息
        locationPoint?.let { builder.add("location_point", it) }
        builder.add("point_type", pointType)

        return builder
    }

    /**
     * 获取设备ID，实现了内存缓存和 SharedPreferences 持久化。
     * 此方法是线程安全的。
     * @return 设备ID字符串。
     */
    @JvmStatic
    @Synchronized
    fun getDeviceId(): String {
        cachedDeviceId?.let { if (it.isNotBlank()) return it }

        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedId = prefs.getString(KEY_DEVICE_ID, null)
        if (!storedId.isNullOrBlank()) {
            cachedDeviceId = storedId
            return storedId
        }

        val newId = try {
            android.provider.Settings.Secure.getString(
                appContext.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: java.util.UUID.randomUUID().toString()

        prefs.edit().putString(KEY_DEVICE_ID, newId).apply()
        cachedDeviceId = newId
        return newId
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocationGranted || coarseLocationGranted
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(): Pair<Double, Double>? {
        if (!hasLocationPermission()) return null

        val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

        // 倒序遍历，优先使用更精确的 GPS
        for (provider in providers.reversed()) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val lastKnownLocation = locationManager.getLastKnownLocation(provider)
                    if (lastKnownLocation != null) {
                        return Pair(lastKnownLocation.latitude, lastKnownLocation.longitude)
                    }
                }
            } catch (e: SecurityException) {
                // 权限检查虽然在调用前做了，但静态分析工具可能仍会报警，捕获异常更安全
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}