package dev.olog.presentation.dialogs.playlist.create

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.QueryMode
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.playlist.CreatePlaylistUseCase
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

@HiltViewModel
class NewPlaylistDialogViewModel @Inject constructor(
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val playingQueueGateway: PlayingQueueGateway,
    private val songGateway: SongGateway,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val playlistGateway: PlaylistGateway,
) : ViewModel() {

    fun getPlaylistTitles(): Collection<String> {
        return playlistGateway.getAll(QueryMode.All).map { it.title }
    }

    suspend fun execute(
        title: String,
        arguments: NewPlaylistDialog.NavArgs
    ): Int? = when (arguments) {
        is NewPlaylistDialog.NavArgs.FromIds -> execute(title, arguments.ids)
        is NewPlaylistDialog.NavArgs.FromMediaId -> execute(title, arguments.mediaId)
    }

    private suspend fun execute(title: String, ids: List<Long>): Int? {
        return createPlaylistUseCase(title, ids)
    }

    private suspend fun execute(title: String, mediaId: MediaId): Int? {
        val tracksToInsert: List<Song> = when (mediaId.category) {
            MediaIdCategory.PLAYING_QUEUE -> playingQueueGateway.getAll().map { it.song }
            MediaIdCategory.SONGS -> listOfNotNull(songGateway.getById(mediaId.id))
            else -> getSongListByParamUseCase(mediaId)
        }
        if (tracksToInsert.isEmpty()) {
            Log.e("NewPlaylist", "no tracks found to be inserted")
            return 0
        }

        return createPlaylistUseCase(title, tracksToInsert)
    }

}