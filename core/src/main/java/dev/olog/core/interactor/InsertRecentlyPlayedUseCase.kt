package dev.olog.core.interactor

import dev.olog.core.author.AuthorGateway
import dev.olog.core.collection.CollectionGateway
import dev.olog.core.MediaUri
import javax.inject.Inject

class InsertRecentlyPlayedUseCase @Inject constructor(
    private val collectionGateway: CollectionGateway,
    private val authorGateway: AuthorGateway,
) {

    suspend operator fun invoke(uri: MediaUri) {
        return when (uri.category) {
            MediaUri.Category.Author -> authorGateway.addToRecentlyPlayed(uri)
            MediaUri.Category.Collection -> collectionGateway.addToRecentlyPlayed(uri)
            MediaUri.Category.Folder,
            MediaUri.Category.Playlist,
            MediaUri.Category.Track,
            MediaUri.Category.Genre -> {}
        }
    }

}