package dev.olog.core.entity

import dev.olog.core.gateway.base.Id

enum class AutoPlaylist {
    LAST_ADDED,
    FAVORITE,
    HISTORY;

    companion object {
        fun isAutoPlaylist(id: Id): Boolean {
            return values().find { it.id == id } != null
        }
    }

    val id: Long
        get() = this.hashCode().toLong()

}