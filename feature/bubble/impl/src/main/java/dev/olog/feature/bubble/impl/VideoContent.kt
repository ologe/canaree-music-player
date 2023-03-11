package dev.olog.feature.bubble.impl

import android.app.Service

class VideoContent(
    service: Service,
) : WebViewContent(service, R.layout.content_web_view) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}