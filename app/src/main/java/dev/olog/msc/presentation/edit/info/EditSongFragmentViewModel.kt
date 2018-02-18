package dev.olog.msc.presentation.edit.info

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.ConnectivityManager
import android.text.TextUtils
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.presentation.edit.info.model.DisplayableSong
import dev.olog.msc.presentation.edit.info.model.UpdateResult
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.isNetworkAvailable
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

class EditSongFragmentViewModel(
        mediaId: MediaId,
        private val lastFmService: LastFmService,
        getSongUseCase: GetSongUseCase,
        private val connectivityManager: ConnectivityManager

) : ViewModel(){

    private val displayedImage = MutableLiveData<String>()
    private val displayedSong = MutableLiveData<DisplayableSong>()
    private val connectivityMessagePush = PublishSubject.create<String>()

    private lateinit var originalSong : Song
    private var getSongDisposable : Disposable? = null

    private var fetchSongInfoDisposable: Disposable? = null
    private var fetchAlbumImageDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        getSongDisposable = getSongUseCase.execute(mediaId)
                .firstOrError()
                .map { it.copy(
                        artist = if (it.artist == AppConstants.UNKNOWN) "" else it.artist,
                        album = if (it.album == AppConstants.UNKNOWN) "" else it.album
                ) }
                .doOnSuccess { this.originalSong = it }
                .subscribe({
                    val song = it.toDisplayableSong()
                    displayedSong.postValue(song)
                    displayedImage.postValue(it.image)
                }, Throwable::printStackTrace)
    }

    fun observeData(): LiveData<DisplayableSong> = displayedSong
    fun observeImage(): LiveData<String> = displayedImage

    fun observeConnectivity() : Observable<String> {
        return connectivityMessagePush
    }

    fun getSongId(): Int = originalSong.id.toInt()

    fun fetchSongInfo(){
        if (connectivityManager.isNetworkAvailable()){
            val song = this.originalSong
            fetchSongInfoDisposable = lastFmService.fetchSongInfo(song.id, song.title, song.artist)
                    .subscribe({ newValue ->
                        val oldValue = displayedSong.value!!
                        displayedSong.postValue(oldValue.copy(
                                title = newValue.title,
                                artist = newValue.artist,
                                album = newValue.album
                        ))
                    }, {
                        displayedSong.postValue(null)
                        it.printStackTrace()
                    })
        } else {
            connectivityMessagePush.onNext("check your internet connection")
        }
    }

    fun fetchAlbumArt() {
        if (connectivityManager.isNetworkAvailable()){
            val song = this.originalSong
            fetchAlbumImageDisposable = lastFmService
                    .fetchAlbumArt(song.id, song.title, song.artist, song.album)
                    .subscribe({
                        displayedImage.postValue(it)
                    }, {
                        displayedImage.postValue(null)
                        it.printStackTrace()
                    })
        } else {
            connectivityMessagePush.onNext("check your internet connection")
        }
    }

    override fun onCleared() {
        getSongDisposable.unsubscribe()
        stopFetching()
    }

    fun stopFetching(){
        fetchSongInfoDisposable.unsubscribe()
        fetchSongInfoDisposable.unsubscribe()
    }

    fun updateMetadata(
            title: String,
            artist: String,
            album: String,
            genre: String,
            year: String,
            disc: String,
            track: String

    ): UpdateResult {
        when {
            title.isBlank() -> return UpdateResult.EMPTY_TITLE
            year.isNotBlank() && !TextUtils.isDigitsOnly(year) -> return UpdateResult.ILLEGAL_YEAR
            disc.isNotBlank() && !TextUtils.isDigitsOnly(disc) -> return UpdateResult.ILLEGAL_DISC_NUMBER
            track.isNotBlank() && !TextUtils.isDigitsOnly(track) -> return UpdateResult.ILLEGAL_TRACK_NUMBER
        }

        try {
            val file = File(originalSong.path)
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault
            tag.setField(FieldKey.TITLE, title)
            if (artist.isNotBlank()){
                tag.setField(FieldKey.ARTIST, artist)
                tag.setField(FieldKey.ALBUM_ARTIST, artist)
            }
            if (album.isNotBlank()){
                tag.setField(FieldKey.ALBUM, album)
            }
            if (genre.isNotBlank()){
                tag.setField(FieldKey.GENRE, genre)
            }
            if (year.isNotBlank()){
                tag.setField(FieldKey.YEAR, year)
            }
            if (disc.isNotBlank()){
                tag.setField(FieldKey.DISC_NO, disc)
            }
            if (track.isNotBlank()){
                tag.setField(FieldKey.TRACK, track)
            }

            audioFile.commit()

            return UpdateResult.OK
        } catch (ex: Exception){
            ex.printStackTrace()
            return UpdateResult.ERROR
        }
    }

    private fun Song.toDisplayableSong(): DisplayableSong {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableSong(
                this.id,
                this.title,
                artist,
                album,
                tag.getFirst(FieldKey.GENRE) ?: "",
                tag.getFirst(FieldKey.YEAR) ?: "",
                tag.getFirst(FieldKey.DISC_NO) ?: "",
                tag.getFirst(FieldKey.TRACK) ?: ""
        )
    }

}