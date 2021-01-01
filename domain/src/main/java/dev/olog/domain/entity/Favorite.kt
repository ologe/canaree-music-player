package dev.olog.domain.entity

import dev.olog.domain.mediaid.MediaId

data class Favorite(
    val trackId: Long,
    val state: State,
    val favoriteType: Type
) {

    enum class State {
        FAVORITE,
        NOT_FAVORITE;

        fun reverse(): State {
            return if (this == FAVORITE) NOT_FAVORITE else FAVORITE
        }

        companion object {
            fun fromBoolean(isFavorite: Boolean): State {
                return if (isFavorite) FAVORITE else NOT_FAVORITE
            }
        }

    }

    enum class Type {
        TRACK,
        PODCAST;

        companion object {

            fun fromMediaId(mediaId: MediaId): Type {
                return if (mediaId.isAnyPodcast) PODCAST else TRACK
            }

        }

    }

}

