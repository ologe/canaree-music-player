package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.FolderGateway
import dev.olog.core.gateway.GenreGateway
import dev.olog.core.gateway.PlaylistGateway
import io.reactivex.Completable
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.insertMostPlayed(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.insertMostPlayed(mediaId)
            MediaIdCategory.GENRES -> genreGateway.insertMostPlayed(mediaId)
            else -> Completable.complete().await()
        }
    }

}