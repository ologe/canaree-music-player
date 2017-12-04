package dev.olog.floating_info

import android.content.Context

class VideoContent(
        context: Context

) : WebViewContent(context) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}