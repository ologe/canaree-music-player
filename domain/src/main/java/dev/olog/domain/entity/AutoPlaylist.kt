package dev.olog.domain.entity

import dev.olog.domain.gateway.base.Id

enum class AutoPlaylist(val id: Long) {
    LAST_ADDED(-1L),
    FAVORITE(-2L),
    HISTORY(-3L);

    companion object {
        fun isAutoPlaylist(id: Id): Boolean {
            return values().find { it.id == id } != null
        }

        fun fromIdOrNull(id: Long): AutoPlaylist? {
            return values().find { it.id == id }
        }

    }

}