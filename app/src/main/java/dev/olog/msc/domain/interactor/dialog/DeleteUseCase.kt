package dev.olog.msc.domain.interactor.dialog

import dev.olog.core.MediaId
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlaylistGateway
import dev.olog.core.gateway.PodcastGateway
import dev.olog.core.gateway.SongGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.interactor.ObserveSongListByParamUseCase
import io.reactivex.Completable
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val podcastGateway: PodcastGateway,
        private val songGateway: SongGateway,
        private val getSongListByParamUseCase: ObserveSongListByParamUseCase

) : CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        if (mediaId.isLeaf && mediaId.isPodcast){
            return podcastGateway.deleteSingle(mediaId.resolveId)
        }

        if (mediaId.isLeaf) {
            return songGateway.deleteSingle(mediaId.resolveId)
        }

        return when {
            mediaId.isPodcastPlaylist -> playlistGateway.deletePlaylist(mediaId.categoryValue.toLong())
            mediaId.isPlaylist -> playlistGateway.deletePlaylist(mediaId.categoryValue.toLong())
            else -> getSongListByParamUseCase(mediaId)
                    .asFlowable()
                    .flatMapCompletable { songGateway.deleteGroup(it) }
        }
    }
}