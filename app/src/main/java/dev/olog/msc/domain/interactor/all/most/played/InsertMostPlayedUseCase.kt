package dev.olog.msc.domain.interactor.all.most.played

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.FolderGateway2
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import io.reactivex.Completable
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
    private val folderGateway: FolderGateway2,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.insertMostPlayed(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.insertMostPlayed(mediaId).await()
            MediaIdCategory.GENRES -> genreGateway.insertMostPlayed(mediaId).await()
            else -> Completable.complete().await()
        }
    }

}