package dev.olog.presentation.edit.song

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.presentation.utils.safeGet
import dev.olog.shared.android.utils.NetworkUtils
import kotlinx.coroutines.*
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditTrackFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presenter: EditTrackFragmentPresenter

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private var fetchJob: Job? = null

    private val songLiveData = MutableLiveData<Song>()
    private val displayableSongLiveData = MutableLiveData<DisplayableSong>()

    fun requestData(mediaId: MediaId) = viewModelScope.launch {
        val song = withContext(Dispatchers.IO) {
            presenter.getSong(mediaId)
        }
        songLiveData.value = song
        displayableSongLiveData.value = song.toDisplayableSong()
    }

    fun observeData(): LiveData<DisplayableSong> = displayableSongLiveData

    fun getOriginalSong(): Song = songLiveData.value!!

    override fun onCleared() {
        fetchJob?.cancel()
        viewModelScope.cancel()
    }

    fun fetchSongInfo(mediaId: MediaId): Boolean {
        if (!NetworkUtils.isConnected(context)) {
            return false
        }
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val lastFmTrack = withContext(Dispatchers.IO) {
                    presenter.fetchData(mediaId.resolveId)
                }
                var currentSong = displayableSongLiveData.value!!
                currentSong = currentSong.copy(
                    title = lastFmTrack?.title ?: currentSong.track,
                    artist = lastFmTrack?.artist ?: currentSong.artist,
                    album = lastFmTrack?.album ?: currentSong.album
                )
                displayableSongLiveData.postValue(currentSong)
            } catch (ex: Throwable){
                displayableSongLiveData.postValue(displayableSongLiveData.value)
            }
        }
        return true
    }

    fun stopFetch() {
        fetchJob?.cancel()
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