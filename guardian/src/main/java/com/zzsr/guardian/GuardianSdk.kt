// guardian/src/main/java/com/zzsr/guardian/GuardianSdk.kt

package com.zzsr.guardian

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.zzsr.guardian.auth.TokenProvider
import com.zzsr.guardian.point.AppLifecycleObserver
import com.zzsr.guardian.point.NetworkUtils
import com.zzsr.guardian.point.ViewClickTracker
import com.zzsr.guardian.settings.SettingsProvider

/**
 * Guardian SDK 的唯一入口和初始化器 (Facade)。
 *
 * 这是一个单例对象，负责管理整个 SDK 的生命周期和配置。
 * 外部应用应通过调用 `GuardianSdk.init()` 来启动所有功能。
 */
object GuardianSdk {

    // 使用 'private set' 保护 config 对象，外部只读
    var config: GuardianConfig? = null
        private set

    private var tokenProvider: TokenProvider? = null
    private var settingsProvider: SettingsProvider? = null
    private var channelId: String? = null
    private var splashAdSpaceId: String? = null

    // 标志位，用于判断 SDK 是否已初始化
    @Volatile
    private var isInitialized = false

    /**
     * [必需] 初始化 Guardian SDK。此方法必须在 Application.onCreate() 中调用一次。
     * 多次调用将被忽略。
     *
     * @param application Application 实例。
     * @param configuration SDK 的完整配置对象。
     */
    @Synchronized
    fun init(application: Application, configuration: GuardianConfig) {
        // 防止重复初始化
        if (isInitialized) {
            return
        }

        // 存储所有配置和提供者
        this.config = configuration
        this.tokenProvider = configuration.tokenProvider
        this.settingsProvider = configuration.settingsProvider
        this.channelId = configuration.channelId
        this.splashAdSpaceId = configuration.splashAdSpaceId

        // 1. 初始化依赖于 Context 的核心子模块
        NetworkUtils.init(application)

        // 2. 注册应用生命周期监听器，用于热启动广告
        val observer = AppLifecycleObserver(application, configuration)
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
        application.registerActivityLifecycleCallbacks(observer)

        // 3. 根据配置初始化可选的功能模块
        if (configuration.enableViewClickTracking) {
            ViewClickTracker.init(application, configuration.viewClickedListener)
        }

        // ... 未来可以在这里添加更多模块的初始化逻辑 ...
        // if (configuration.enableProxyDetection) {
        //     ProxyDetector.init(application, configuration.proxyDetectedListener)
        // }
        // if (configuration.enableVirtualDeviceDetection) {
        //     VirtualDeviceDetector.init(application, configuration.virtualDeviceDetectedListener)
        // }

        isInitialized = true
    }

    /**
     * [内部使用] 获取用户 Token。
     * 通过 TokenProvider 接口实时获取。
     * @return 当前的 Token 字符串，或 null。
     */
    internal fun getToken(): String? {
        return tokenProvider?.getToken()
    }

    /**
     * [内部使用] 检查是否是首次运行。
     * 通过 SettingsProvider 接口查询。
     * @return 如果是首次运行，返回 true；否则返回 false。
     */
    internal fun isFirstRun(): Boolean {
        // 如果未初始化或 provider 为 null，提供一个安全的默认值（false 表示不是首次运行）
        return settingsProvider?.isFirstRun() ?: false
    }

    /**
     * [内部使用] 获取渠道 ID。
     * @return 配置的渠道 ID，或 "unknown"。
     */
    internal fun getChannelId(): String {
        return channelId ?: "unknown"
    }

    /**
     * [内部使用] 获取开屏广告位 ID。
     * @return 配置的开屏广告位 ID，或空字符串。
     */
    internal fun getSplashAdSpaceId(): String {
        // 返回空字符串，让广告 SDK 自己处理错误，比返回 null 更安全
        return splashAdSpaceId ?: ""
    }
}