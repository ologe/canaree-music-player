package dev.olog.core.entity

import android.provider.MediaStore

data class LastMetadata(
    val title: String,
    val subtitle: String,
    val id: Long
) {

    fun isNotEmpty(): Boolean {
        return title.isNotBlank()
    }

    val description: String
        get() {
            if (subtitle == MediaStore.UNKNOWN_STRING) {
                return title
            }
            return "$title $subtitle"
        }

}