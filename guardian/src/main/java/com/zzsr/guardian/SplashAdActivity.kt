// guardian/src/main/java/com/zzsr/guardian/SplashAdActivity.kt

package com.zzsr.guardian

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import xyz.adscope.amps.ad.splash.AMPSSplashAd
import xyz.adscope.amps.ad.splash.AMPSSplashLoadEventListener
import xyz.adscope.amps.common.AMPSError
import xyz.adscope.amps.config.AMPSRequestParameters
import xyz.adscope.amps.tool.util.AMPSScreenUtil

/**
 * 一个专用于展示热启动开屏广告的 Activity。
 *
 * 这个 Activity 必须通过包含 `EXTRA_IS_HOT_START` extra 的 Intent 启动。
 * 它负责加载和展示广告，并在流程结束后（成功、失败或超时）自动关闭，
 * 显示其下方的 Activity。
 */
class SplashAdActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashAdActivity"

        /**
         * [必需] 启动此 Activity 时 Intent 中必须包含的 extra key。
         * 其值应为 true，以表明这是一次合法的热启动。
         */
        const val EXTRA_IS_HOT_START = "IS_HOT_START"

        // 设置一个广告加载的全局超时时间，防止用户长时间等待
        private const val AD_LOAD_TIMEOUT_MS = 5000L
    }

    private lateinit var splashContainer: FrameLayout
    private var splashAd: AMPSSplashAd? = null
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private var hasFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 安全检查：确保此 Activity 是被正确启动的
        if (!intent.getBooleanExtra(EXTRA_IS_HOT_START, false)) {
            Log.e(TAG, "SplashAdActivity was started incorrectly without the required hot-start flag. Finishing immediately.")
            finish()
            return // 立即返回，不执行后续代码
        }

        // 禁用返回按钮，防止用户在广告展示期间退出
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "Back button pressed, but disabled for splash ad.")
            }
        })

        // 这里需要一个布局文件，至少包含一个 FrameLayout 作为广告容器
        // 假设布局文件是 R.layout.activity_splash_ad
        // setContentView(R.layout.activity_splash_ad)
        // 为了让库独立，我们可以动态创建一个 FrameLayout
        setupContentView()


        // 启动超时计时器，作为广告 SDK 回调失败的“安全网”
        timeoutHandler.postDelayed({
            Log.w(TAG, "Ad loading timed out after ${AD_LOAD_TIMEOUT_MS}ms.")
            finishSplashFlow()
        }, AD_LOAD_TIMEOUT_MS)

        // 开始加载广告
        loadSplashAd()
    }

    private fun setupContentView() {
        splashContainer = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(splashContainer)
    }


    private fun loadSplashAd() {
        // 通过 GuardianSdk 获取解耦后的广告位ID
        val adSpaceId = GuardianSdk.getSplashAdSpaceId()
        if (adSpaceId.isBlank()) {
            Log.e(TAG, "Splash Ad Space ID is blank. Finishing flow.")
            finishSplashFlow()
            return
        }

        val params = AMPSRequestParameters.Builder()
            .setSpaceId(adSpaceId)
            .setTimeOut(3000) // SDK 内部超时，可以比我们的总超时短
            .setWidth(AMPSScreenUtil.getScreenWidth(this))
            .setHeight(AMPSScreenUtil.getScreenHeight(this))
            .build()

        splashAd = AMPSSplashAd(this, params, object : AMPSSplashLoadEventListener {
            override fun onAmpsAdLoaded() {
                Log.d(TAG, "Ad loaded successfully.")
                if (!isFinishing && !isDestroyed) {
                    splashAd?.show(splashContainer)
                }
            }

            override fun onAmpsAdFailed(error: AMPSError) {
                Log.e(TAG, "Ad failed to load - Code: ${error.code}, Msg: ${error.message}")
                Toast.makeText(this@SplashAdActivity, "Ad failed to load", Toast.LENGTH_SHORT).show()
                finishSplashFlow()
            }

            override fun onAmpsAdShow() {
                Log.d(TAG, "Ad shown.")
                // 广告成功展示，可以移除我们自己的超时任务，因为此时广告SDK会接管流程
                timeoutHandler.removeCallbacksAndMessages(null)
            }

            override fun onAmpsAdClicked() {
                Log.d(TAG, "Ad clicked.")
            }

            override fun onAmpsAdDismiss() {
                Log.d(TAG, "Ad dismissed.")
                // 广告关闭，结束流程
                finishSplashFlow()
            }
        })

        splashAd?.loadAd()
    }

    /**
     * 安全地结束开屏广告流程。
     * 这个方法确保流程只被结束一次，并以无动画的方式关闭 Activity。
     */
    private fun finishSplashFlow() {
        // 使用同步锁确保多线程安全，并只执行一次
        synchronized(this) {
            if (hasFinished) {
                return
            }
            hasFinished = true
        }

        // 因为这个 Activity 专用于热启动，所以流程结束后总是直接 finish()
        // 以便显示其下方的、用户之前正在浏览的 Activity。
        finish()

        // 禁用退出动画，实现平滑过渡
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        // 清理资源，防止内存泄漏
        timeoutHandler.removeCallbacksAndMessages(null)
        // 如果广告SDK提供了销毁方法，应该在这里调用
        // splashAd?.destroy()
        splashAd = null
        super.onDestroy()
    }
}