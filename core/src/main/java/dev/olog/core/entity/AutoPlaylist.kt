package dev.olog.core.entity

enum class AutoPlaylist {
    LAST_ADDED,
    FAVORITE,
    HISTORY;

    companion object {
        fun isAutoPlaylist(id: Long): Boolean {
            return values().find { it.id == id } != null
        }
    }

    val id: Long
        get() = this.hashCode().toLong()

}