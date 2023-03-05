package dev.olog.service.floating

import android.app.Service
import android.content.Context
import androidx.lifecycle.LifecycleOwner

class VideoContent(
    service: Service,
) : WebViewContent(service, R.layout.content_web_view) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}