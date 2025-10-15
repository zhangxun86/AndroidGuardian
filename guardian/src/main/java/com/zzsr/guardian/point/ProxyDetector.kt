package com.zzsr.guardian.point

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.net.Proxy
import java.net.ProxySelector

object ProxyDetector {

    /**
     * 综合判断设备是否正在使用代理网络。
     * 结合了系统设置、VPN状态和Java网络代理配置的检查。
     * @param context Context for accessing system services.
     * @return "1" if a proxy is likely in use, "0" otherwise.
     */
    fun isProxyNetwork(context: Context): String {
        return if (isSystemProxySet() || isVpnActive(context) || isJavaProxySet()) {
            "1"
        } else {
            "0"
        }
    }

    /**
     * 方法一：检查系统级别的 HTTP 代理设置。
     * 这是用户在 Wi-Fi 设置中手动配置的代理。
     * @return `true` if a system-wide HTTP proxy is configured, `false` otherwise.
     */
    private fun isSystemProxySet(): Boolean {
        // System.getProperty is the standard way to check for Java VM-wide proxy settings.
        // Android's system proxy settings are usually propagated to these properties.
        val proxyHost = System.getProperty("http.proxyHost")
        val proxyPort = System.getProperty("http.proxyPort")
        return !proxyHost.isNullOrBlank() && !proxyPort.isNullOrBlank()
    }

    /**
     * 方法二：检查设备是否有活动的 VPN 连接。
     * VPN 是最常见的代理形式之一。
     * @param context Context for accessing ConnectivityManager.
     * @return `true` if a VPN connection is active, `false` otherwise.
     */
    @SuppressLint("MissingPermission")
    private fun isVpnActive(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            // NETWORK_CAPABILITY_VPN is a strong indicator of an active VPN.
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } else {
            // For older versions, we can check for a network of type TYPE_VPN.
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_VPN)
            return networkInfo?.isConnectedOrConnecting == true
        }
    }

    /**
     * 方法三：检查 Java 的 ProxySelector 配置。
     * 一些应用或库可能会通过这种方式设置代理，而不修改系统属性。
     * @return `true` if a non-direct proxy is configured via ProxySelector.
     */
    private fun isJavaProxySet(): Boolean {
        return try {
            val proxySelector = ProxySelector.getDefault()
            val proxies = proxySelector.select(java.net.URI("https://www.baidu.com"))
            // If the list contains any proxy that is not Proxy.NO_PROXY, a proxy is set.
            proxies.any { it != Proxy.NO_PROXY }
        } catch (e: Exception) {
            // An exception might occur, in which case we assume no proxy.
            e.printStackTrace()
            false
        }
    }
}