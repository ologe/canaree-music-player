package dev.olog.floating_info

import android.content.Context

class LyricsContent(
        context: Context
) : WebViewContent(context) {

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}