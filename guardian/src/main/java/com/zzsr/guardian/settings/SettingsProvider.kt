// guardian/src/main/java/com/zzsr/guardian/settings/SettingsProvider.kt

package com.zzsr.guardian.settings

/**
 * 设置提供者接口。
 * 使用 Guardian SDK 的应用必须实现此接口，以便 SDK 可以查询应用级别的一些配置。
 */
interface SettingsProvider {
    /**
     * 检查应用是否是首次运行。
     * @return 如果是首次运行，返回 true；否则返回 false。
     */
    fun isFirstRun(): Boolean

    // 未来如果需要其他设置，可以在这里添加更多方法
    // fun isAnalyticsEnabled(): Boolean
}