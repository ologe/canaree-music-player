package dev.olog.service.floating

import android.content.Context
import androidx.lifecycle.Lifecycle

class VideoContent(
    lifecycle: Lifecycle,
    context: Context

) : WebViewContent(lifecycle, context, R.layout.content_web_view) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}