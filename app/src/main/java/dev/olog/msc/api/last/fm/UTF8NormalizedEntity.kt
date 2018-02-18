package dev.olog.msc.api.last.fm

import java.net.URLEncoder

class UTF8NormalizedEntity(
        value: String
) {

    val value : String = URLEncoder.encode(value, "UTF-8")

}