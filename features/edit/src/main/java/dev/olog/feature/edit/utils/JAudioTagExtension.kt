package dev.olog.feature.edit.utils

import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag

internal fun Tag?.safeGet(fieldKey: FieldKey): String {
    return this?.getFirst(fieldKey) ?: ""
}