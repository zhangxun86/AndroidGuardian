// guardian/src/main/java/com/zzsr/guardian/point/ViewClickTracker.kt

package com.zzsr.guardian.point

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zzsr.guardian.GuardianSdk
import java.lang.ref.WeakReference
import java.lang.reflect.Field

/**
 * 全局视图点击事件追踪器。
 * - 通过 Hook View 的 OnClickListener 来自动监听所有可点击视图的事件。
 * - 提取视图和页面信息，并通过 OnViewClickedListener 回调出去。
 * - 所有外部依赖（如设置）都通过 GuardianSdk 获取。
 */
object ViewClickTracker {

    private const val TAG = "ViewClickTracker"
    // 使用 WeakReference 存储 listener，以防内存泄漏
    private var viewClickedListener: WeakReference<OnViewClickedListener>? = null

    // 用于标记已经 Hook 过的 View，避免重复 Hook
    private val R_ID_TAG_KEY = "view_click_tracker_hook_tag".hashCode()

    /**
     * [必需] 初始化点击追踪器。此方法由 GuardianSdk.init() 根据配置自动调用。
     * @param application Application 实例。
     * @param listener [可选] 点击事件的回调监听器。
     */
    internal fun init(application: Application, listener: OnViewClickedListener?) {
        this.viewClickedListener = listener?.let { WeakReference(it) }
        application.registerActivityLifecycleCallbacks(AppActivityLifecycleCallbacks())
    }

    private class AppActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            // 在 Activity 可见时，遍历并 Hook 它的视图树
            val decorView = activity.window.decorView
            traverseAndHookViews(decorView)
        }
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    private fun traverseAndHookViews(view: View?) {
        if (view == null) return

        // 如果视图是可点击的，并且已经设置了监听器，就尝试 Hook 它
        if (view.isClickable && getOriginalOnClickListener(view) != null) {
            hookClickListener(view)
        }

        // 如果是 ViewGroup，则递归遍历其子视图
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                traverseAndHookViews(view.getChildAt(i))
            }
        }
    }

    private fun hookClickListener(view: View) {
        // 检查是否已经 Hook 过
        if (view.getTag(R_ID_TAG_KEY) != null) {
            return
        }

        val originalListener = getOriginalOnClickListener(view)
        // 确保原始监听器存在，并且不是我们自己的包装类（防止重复包装）
        if (originalListener != null && originalListener !is WrappingOnClickListener) {
            val wrappedListener = WrappingOnClickListener(originalListener)
            view.setOnClickListener(wrappedListener)
            view.setTag(R_ID_TAG_KEY, "hooked")
        }
    }

    /**
     * 使用反射获取 View 内部的 mOnClickListener 实例。
     */
    private fun getOriginalOnClickListener(view: View): View.OnClickListener? {
        return try {
            val getListenerInfo = View::class.java.getDeclaredMethod("getListenerInfo")
            getListenerInfo.isAccessible = true
            val listenerInfo = getListenerInfo.invoke(view) ?: return null
            val listenerInfoClass = listenerInfo.javaClass
            val onClickListenerField: Field = listenerInfoClass.getDeclaredField("mOnClickListener")
            onClickListenerField.isAccessible = true
            onClickListenerField.get(listenerInfo) as? View.OnClickListener
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get original OnClickListener via reflection.", e)
            null
        }
    }

    /**
     * 从 Context 中提取其所属的 Activity 实例。
     */
    private fun getActivityFromContext(context: Context?): Activity? {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }

    /**
     * OnClickListener 的包装类，用于注入我们的追踪逻辑。
     */
    private class WrappingOnClickListener(
        private val original: View.OnClickListener
    ) : View.OnClickListener {

        override fun onClick(v: View?) {
            // 1. 执行我们的追踪和回调逻辑
            // 通过 GuardianSdk 查询是否为首次运行，如果不是，则执行追踪
            if (!GuardianSdk.isFirstRun()) {
                val eventInfo = createEventInfo(v)
                Log.d(TAG, "View clicked: $eventInfo")
                // 将事件信息通过回调传递出去
                viewClickedListener?.get()?.onViewClicked(eventInfo)
            }

            // 2. 调用原始的点击逻辑，确保原有功能不受影响
            original.onClick(v)
        }

        /**
         * 创建包含点击事件详细信息的 Map。
         */
        private fun createEventInfo(view: View?): Map<String, String> {
            if (view == null) return mapOf("page" to "unknown_view")

            val info = mutableMapOf<String, String>()

            // 提取视图 ID
            info["id"] = try {
                if (view.id != View.NO_ID) view.resources.getResourceEntryName(view.id) else "no_id"
            } catch (e: Exception) {
                "no_id"
            }

            // 提取视图类型
            info["type"] = view.javaClass.simpleName

            // 提取文本内容（如果存在）
            if (view is TextView) {
                view.text?.toString()?.take(50)?.let {
                    if (it.isNotBlank()) info["text"] = it
                }
            }

            // 提取页面信息
            val activity = getActivityFromContext(view.context)
            info["page"] = activity?.javaClass?.name ?: "unknown_page"

            return info
        }
    }
}