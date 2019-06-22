package dev.olog.msc.presentation.dialog.create.playlist

import dev.olog.presentation.model.PlaylistType
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastUseCase
import dev.olog.msc.domain.interactor.item.GetSongUseCase
import dev.olog.msc.domain.interactor.playing.queue.GetPlayingQueueUseCase
import dev.olog.msc.domain.interactor.playlist.InsertCustomTrackListRequest
import dev.olog.msc.domain.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Completable
import javax.inject.Inject

class NewPlaylistDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    playlists: GetPlaylistsBlockingUseCase,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val getSongUseCase: GetSongUseCase,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getPlayinghQueueUseCase: GetPlayingQueueUseCase

) {

    private val playlistType = if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK

    private val existingPlaylists = playlists.execute(playlistType)
            .map { it.title.toLowerCase() }

    fun execute(playlistTitle: String) : Completable {
        if (mediaId.isPlayingQueue){
            return getPlayinghQueueUseCase.execute().mapToList { it.id }
                    .flatMapCompletable {
                        insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(playlistTitle, it, playlistType))
                    }
        }

        return if (mediaId.isLeaf && mediaId.isPodcast) {
            getPodcastUseCase.execute(mediaId).firstOrError().map { listOf(it.id) }
                    .flatMapCompletable {
                        insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(playlistTitle, it, playlistType))
                    }
        } else if (mediaId.isLeaf) {
            getSongUseCase.execute(mediaId).firstOrError().map { listOf(it.id) }
                    .flatMapCompletable {
                        insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(playlistTitle, it, playlistType))
                    }
        } else {
            getSongListByParamUseCase.execute(mediaId).firstOrError().mapToList { it.id }
                    .flatMapCompletable {
                        insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(playlistTitle, it, playlistType))
                    }
        }
    }

    fun isStringValid(string: String): Boolean {
        return !existingPlaylists.contains(string.toLowerCase())
    }

}