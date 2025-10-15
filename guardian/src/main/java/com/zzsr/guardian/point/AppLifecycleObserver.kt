// guardian/src/main/java/com/zzsr/guardian/point/AppLifecycleObserver.kt

package com.zzsr.guardian.point

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zzsr.guardian.GuardianConfig
import com.zzsr.guardian.SplashAdActivity
import java.util.concurrent.TimeUnit

/**
 * 应用全局生命周期观察者。
 * - 监听应用从后台返回前台的事件。
 * - 根据配置决定是否在满足时间间隔后展示热启动开屏广告。
 * - 通过 ActivityLifecycleCallbacks 追踪当前顶层 Activity，以避免在广告页上重复弹出广告。
 *
 * @property application Application 实例，用于启动 Activity。
 * @property config Guardian SDK 的配置对象，所有行为都基于此配置。
 */
internal class AppLifecycleObserver(
    private val application: Application,
    private val config: GuardianConfig
) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    companion object {
        private const val TAG = "AppLifecycleObserver"
    }

    // 从配置中动态计算广告展示的时间间隔（单位：毫秒）
    private val adShowIntervalMillis = TimeUnit.SECONDS.toMillis(config.hotStartAdIntervalSeconds)

    // 记录应用进入后台的时间戳
    private var backgroundTimestamp: Long = 0

    // 记录当前顶层的 Activity 实例
    private var currentActivity: Activity? = null

    /**
     * 当应用进程的生命周期进入 ON_START 时调用，通常意味着应用从后台返回前台。
     */
    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "App is returning to foreground.")

        // 检查是否满足展示热启动广告的所有条件
        if (shouldShowHotStartAd()) {
            // 检查当前栈顶的 Activity 是不是广告页本身，防止在广告页上重复打开
            if (currentActivity?.javaClass != config.splashAdActivityClass) {
                Log.i(TAG, "Interval exceeded ${config.hotStartAdIntervalSeconds} seconds. Showing splash ad.")
                showSplashAd()
            } else {
                Log.d(TAG, "Already on splash ad activity, skipping.")
            }
        }

        // 无论是否展示广告，一旦返回前台，就重置后台时间戳
        backgroundTimestamp = 0
    }

    /**
     * 当应用进程的生命周期进入 ON_STOP 时调用，通常意味着应用进入后台。
     */
    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "App is going to background.")
        // 记录进入后台的精确时间
        backgroundTimestamp = System.currentTimeMillis()
    }

    /**
     * 判断是否应该展示热启动广告。
     * @return 如果所有条件都满足，则返回 true。
     */
    private fun shouldShowHotStartAd(): Boolean {
        // 1. 检查配置中是否启用了热启动广告 (次数大于0)
        val isAdEnabled = config.hotStartAdCount > 0
        // 2. 检查是否确实是从后台返回 (backgroundTimestamp > 0)
        val isFromBackground = backgroundTimestamp > 0
        // 3. 检查后台停留时间是否超过了配置的阈值
        val isTimeExceeded = System.currentTimeMillis() - backgroundTimestamp > adShowIntervalMillis

        return isAdEnabled && isFromBackground && isTimeExceeded
    }

    /**
     * 启动开屏广告 Activity。
     */
    private fun showSplashAd() {
        // 使用配置中传入的 Activity Class 来创建 Intent，而不是写死
        val intent = Intent(application, config.splashAdActivityClass).apply {
            // 必须添加 NEW_TASK 标志，因为我们是从 Application Context 启动 Activity
            // 添加 NO_ANIMATION 标志以禁用启动动画
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)

            // 传入一个 extra 标志，告知 SplashAdActivity 这是一次“热启动”
            putExtra(SplashAdActivity.EXTRA_IS_HOT_START, true)
        }
        application.startActivity(intent)
    }

    // --- 以下是 Application.ActivityLifecycleCallbacks 的实现 ---
    // 主要作用是实时追踪当前位于前台的 Activity (currentActivity)。

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        // 当一个 Activity 进入 Resumed 状态时，它就是当前的用户可见页面
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        // 如果被销毁的 Activity 正好是我们记录的当前 Activity，则将其置空
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}