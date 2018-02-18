package dev.olog.msc.api.last.fm

import java.net.URLEncoder

class NormalizedEntity(
        value: String
) {

    val value : String = URLEncoder.encode(value, "UTF-8")

}