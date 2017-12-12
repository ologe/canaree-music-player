package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val songGateway: SongGateway,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : CompletableUseCaseWithParam<String>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: String): Completable {
        val category = MediaIdHelper.extractCategory(mediaId)
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)

        if (MediaIdHelper.isSong(mediaId) || category == MediaIdHelper.MEDIA_ID_BY_ALL) {
            val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
            return songGateway.deleteSingle(songId)
        }

        return when (category){
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistGateway.deletePlaylist(categoryValue.toLong())
            else -> getSongListByParamUseCase.execute(mediaId)
                    .flatMapCompletable { songGateway.deleteGroup(it) }
        }
    }
}