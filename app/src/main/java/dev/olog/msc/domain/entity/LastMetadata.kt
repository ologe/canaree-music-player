package dev.olog.msc.domain.entity

import dev.olog.msc.constants.AppConstants

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
            if (subtitle == AppConstants.UNKNOWN){
                return title
            }
            return "$title $subtitle"
        }

}