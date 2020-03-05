package dev.olog.presentation.utils

import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import timber.log.Timber

fun Tag?.safeGet(fieldKey: FieldKey): String {
    return try {
        this!!.getFirst(fieldKey)
    } catch (ex: Exception){
        Timber.e(ex)
        ""
    }
}