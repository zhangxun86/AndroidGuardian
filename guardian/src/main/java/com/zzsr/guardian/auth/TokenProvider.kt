// guardian/src/main/java/com/zzsr/guardian/auth/TokenProvider.kt

package com.zzsr.guardian.auth

/**
 * Token 提供者接口。
 * 这是一个“契约”，任何使用 Guardian SDK 的应用都必须提供一个此接口的实现，
 * 以便 SDK 在需要时能够获取到认证 Token。
 *
 * 使用 'fun interface' 使其成为一个 SAM (Single Abstract Method) 接口，
 * 在 Kotlin 中可以用 lambda 表达式更简洁地实现。
 */
fun interface TokenProvider {
    /**
     * 获取当前的认证 Token。
     * @return 当前用户的 Token 字符串，如果用户未登录或 Token 不可用，则返回 null。
     */
    fun getToken(): String?
}