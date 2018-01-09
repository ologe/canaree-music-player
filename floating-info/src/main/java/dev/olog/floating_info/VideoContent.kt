package dev.olog.floating_info

import android.arch.lifecycle.Lifecycle
import android.content.Context

class VideoContent(
        lifecycle: Lifecycle,
        context: Context

) : WebViewContent(lifecycle, context, R.layout.content_web_view) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}