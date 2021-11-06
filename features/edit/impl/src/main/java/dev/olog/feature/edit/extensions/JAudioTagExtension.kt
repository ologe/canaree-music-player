package dev.olog.feature.edit.extensions

import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag

fun Tag?.safeGet(fieldKey: FieldKey): String {
    return try {
        this!!.getFirst(fieldKey)
    } catch (ex: Throwable){
        ex.printStackTrace()
        ""
    }
}