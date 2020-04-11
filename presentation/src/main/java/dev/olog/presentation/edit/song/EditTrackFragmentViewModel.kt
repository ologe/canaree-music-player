package dev.olog.presentation.edit.song

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.entity.track.Song
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.lib.audio.tagger.AudioTagger
import dev.olog.shared.android.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class EditTrackFragmentViewModel @Inject constructor(
    private val context: Context,
    private val presenter: EditTrackFragmentPresenter,
    private val audioTagger: AudioTagger,
    private val schedulers: Schedulers

) : ViewModel() {

    private var fetchJob by autoDisposeJob()

    private val songPublisher = ConflatedBroadcastChannel<Song>()
    private val displayableSongPublisher = ConflatedBroadcastChannel<DisplayableSong>()

    fun requestData(mediaId: PresentationId.Track) = viewModelScope.launch {
        val song = withContext(schedulers.io) {
            presenter.getSong(mediaId)
        }
        songPublisher.offer(song)
        displayableSongPublisher.offer(song.toDisplayableSong())
    }

    override fun onCleared() {
        super.onCleared()
        songPublisher.close()
        displayableSongPublisher.close()
    }

    fun observeData(): Flow<DisplayableSong> = displayableSongPublisher.asFlow()

    fun getOriginalSong(): Song = songPublisher.value

    fun fetchSongInfo(mediaId: PresentationId.Track): Boolean {
        if (!NetworkUtils.isConnected(context)) {
            return false
        }
        fetchJob = viewModelScope.launch {
            try {
                val lastFmTrack = withContext(Dispatchers.IO) {
                    presenter.fetchData(mediaId.id.toLong())
                }
                var currentSong = displayableSongPublisher.value
                currentSong = currentSong.copy(
                    title = lastFmTrack?.title ?: currentSong.track,
                    artist = lastFmTrack?.artist ?: currentSong.artist,
                    album = lastFmTrack?.album ?: currentSong.album
                )
                displayableSongPublisher.offer(currentSong)
            } catch (ex: Exception){
                Timber.e(ex)
                displayableSongPublisher.offer(displayableSongPublisher.value)
            }
        }
        return true
    }

    fun stopFetch() {
        fetchJob = null
    }

    private fun Song.toDisplayableSong(): DisplayableSong {
        val file = File(path)
        val tags = audioTagger.read(file)

        return DisplayableSong(
            id = this.id,
            artistId = this.artistId,
            albumId = this.albumId,
            title = tags.title ?: this.title,
            artist = tags.artist ?: this.artist,
            albumArtist = tags.albumArtist ?: this.albumArtist,
            album = tags.album ?: this.album,
            genre = tags.genre ?: "",
            year = tags.year ?: "",
            disc = tags.discNo ?: this.discNumber.toString(),
            track = tags.trackNo ?: this.trackNumber.toString(),
            path = this.path,
            bitrate = tags.bitrate,
            format = tags.format,
            sampling = tags.sampling,
            isPodcast = this.isPodcast
        )
    }

}