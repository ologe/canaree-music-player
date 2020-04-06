package dev.olog.core.entity

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
            if (subtitle == "<unknown>") {
                return title
            }
            return "$title $subtitle"
        }

}