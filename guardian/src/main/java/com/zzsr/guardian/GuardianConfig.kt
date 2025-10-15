// guardian/src/main/java/com/zzsr/guardian/GuardianConfig.kt

package com.zzsr.guardian

import android.app.Activity
import com.zzsr.guardian.auth.TokenProvider
import com.zzsr.guardian.point.OnViewClickedListener
import com.zzsr.guardian.settings.SettingsProvider

/**
 * Guardian SDK 的统一配置类。
 * 在初始化 SDK 时，必须构建并传入一个此类的实例。
 */
data class GuardianConfig(
    /**
     * [必需] 热启动时要打开的广告页 Activity 的 Class 对象。
     */
    val splashAdActivityClass: Class<out Activity>,

    /**
     * [必需] Token 提供者的实现，用于在需要时获取用户 Token。
     */
    val tokenProvider: TokenProvider,

    /**
     * [必需] 应用设置提供者的实现，用于查询应用状态（如是否首次运行）。
     */
    val settingsProvider: SettingsProvider,

    /**
     * [必需] 热启动开屏广告所使用的广告位ID (Space ID)。
     */
    val splashAdSpaceId: String,

    /**
     * 热启动时展示广告的次数。如果为 0 或更小，则禁用热启动广告。
     * 默认值为 0 (禁用)。
     */
    val hotStartAdCount: Int = 0,

    /**
     * 定义应用从后台返回前台时，需要超过多少秒才展示广告。
     * 默认值为 10 秒。
     */
    val hotStartAdIntervalSeconds: Long = 10L,

    /**
     * 应用的渠道ID。这个ID将附加到网络请求的 version 参数中。
     * 默认值为 "unknown"。
     */
    val channelId: String = "unknown",

    /**
     * 是否启用全局视图点击事件追踪。
     * 默认值为 false。
     */
    val enableViewClickTracking: Boolean = false,

    /**
     * [可选] 视图点击事件的回调监听器。
     */
    val viewClickedListener: OnViewClickedListener? = null
)