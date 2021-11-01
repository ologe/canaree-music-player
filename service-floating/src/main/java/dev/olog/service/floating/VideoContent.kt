package dev.olog.service.floating

import android.content.Context
import androidx.lifecycle.LifecycleOwner

class VideoContent(
    lifecycleOwner: LifecycleOwner,
    context: Context

) : WebViewContent(lifecycleOwner, context, R.layout.content_web_view) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}