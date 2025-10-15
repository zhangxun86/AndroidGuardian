// guardian/src/main/java/com/zzsr/guardian/point/OnViewClickedListener.kt

package com.zzsr.guardian.point

/**
 * 视图点击事件的回调监听器接口。
 * 当 ViewClickTracker 捕获到一个点击事件时，会通过此接口将事件信息传递出去。
 */
fun interface OnViewClickedListener {
    /**
     * 当一个被追踪的视图被点击时调用。
     * @param eventInfo 一个包含点击事件详细信息的 Map。
     *                  常见的 key 包括:
     *                  - "id": 视图的资源ID名 (e.g., "login_button")
     *                  - "type": 视图的类名 (e.g., "AppCompatButton")
     *                  - "text": 视图的文本内容 (如果存在)
     *                  - "page": 视图所在的 Activity 的完整类名
     */
    fun onViewClicked(eventInfo: Map<String, String>)
}