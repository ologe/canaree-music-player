package dev.olog.data.playlist

sealed interface PlaylistResolver {

    val id: String

    object LastAdded : PlaylistResolver { override val id: String = LastAddedId }
    object Favourites : PlaylistResolver { override val id: String = FavouritesId }
    object History : PlaylistResolver { override val id: String = HistoryId }
    data class MediaStore(override val id: String) : PlaylistResolver

    companion object {
        const val LastAddedId = "last_added"
        const val FavouritesId = "favourites"
        const val HistoryId = "history"

        fun fromId(id: String): PlaylistResolver = when (id) {
            LastAddedId -> LastAdded
            FavouritesId -> Favourites
            HistoryId -> History
            else -> MediaStore(id)
        }

    }

}