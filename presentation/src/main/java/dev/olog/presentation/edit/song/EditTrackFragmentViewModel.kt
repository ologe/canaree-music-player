package dev.olog.presentation.edit.song

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.entity.track.Song
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.utils.safeGet
import dev.olog.shared.android.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class EditTrackFragmentViewModel @Inject constructor(
    private val context: Context,
    private val presenter: EditTrackFragmentPresenter,
    private val schedulers: Schedulers

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

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
        val audioFile = AudioFileIO.read(file)
        val audioHeader = audioFile.audioHeader
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableSong(
            id = this.id,
            artistId = this.artistId,
            albumId = this.albumId,
            title = this.title,
            artist = tag.safeGet(FieldKey.ARTIST),
            albumArtist = tag.safeGet(FieldKey.ALBUM_ARTIST),
            album = this.album,
            genre = tag.safeGet(FieldKey.GENRE),
            year = tag.safeGet(FieldKey.YEAR),
            disc = tag.safeGet(FieldKey.DISC_NO),
            track = tag.safeGet(FieldKey.TRACK),
            path = this.path,
            bitrate = audioHeader.bitRate + " kb/s",
            format = audioHeader.format,
            sampling = audioHeader.sampleRate + " Hz",
            isPodcast = this.isPodcast
        )
    }

}