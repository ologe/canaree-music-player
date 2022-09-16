package dev.olog.core.entity

@Deprecated("")
enum class AutoPlaylist {
    LAST_ADDED,
    FAVORITE,
    HISTORY;

    companion object {
        @JvmStatic
        fun isAutoPlaylist(id: Long): Boolean {
            return values().find { it.id == id } != null
        }
    }

    val id: Long
        get() = this.hashCode().toLong()

}