package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val songGateway: SongGateway,
        private val genreGateway: GenreGateway

) : CompletableUseCaseWithParam<String>(scheduler) {

    override fun buildUseCaseObservable(mediaId: String): Completable {
        val category = MediaIdHelper.extractCategory(mediaId)
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)

        return when (category){
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistGateway.deletePlaylist(categoryValue.toLong())
            MediaIdHelper.MEDIA_ID_BY_ALL -> {
                val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
                songGateway.deleteSingle(songId)
            }
            MediaIdHelper.MEDIA_ID_BY_GENRE -> genreGateway.deleteGenre(categoryValue.toLong())
            else -> songGateway.deleteGroup(mediaId)
        }
    }
}