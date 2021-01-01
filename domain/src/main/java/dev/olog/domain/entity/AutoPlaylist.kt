package dev.olog.domain.entity

import dev.olog.domain.gateway.base.Id

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