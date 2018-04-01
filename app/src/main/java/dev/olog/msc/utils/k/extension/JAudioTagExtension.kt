package dev.olog.msc.utils.k.extension

import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag

fun Tag?.get(fieldKey: FieldKey): String {
    return try {
        this!!.getFirst(fieldKey)
    } catch (ex: Exception){
        ""
    }
}