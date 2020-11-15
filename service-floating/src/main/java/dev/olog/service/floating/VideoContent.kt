package dev.olog.service.floating

import androidx.lifecycle.LifecycleService

class VideoContent(
    service: LifecycleService,
) : WebViewContent(service, R.layout.content_web_view) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}