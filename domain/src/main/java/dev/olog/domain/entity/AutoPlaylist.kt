package dev.olog.domain.entity

enum class AutoPlaylist {
    LAST_ADDED,
    FAVORITE,
    HISTORY;

    companion object {
        @JvmStatic
        fun isAutoPlaylist(id: Long): Boolean {
            return values().find { it.id == id } != null
        }

        @JvmStatic
        fun isAutoPlaylist(id: String): Boolean {
            return values().find { it.id == id.toLongOrNull() } != null
        }
    }

    val id: Long
        get() = this.hashCode().toLong()

}