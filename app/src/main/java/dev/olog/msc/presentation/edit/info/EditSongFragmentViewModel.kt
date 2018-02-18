package dev.olog.msc.presentation.edit.info

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.presentation.edit.info.model.DisplayableSong
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

class EditSongFragmentViewModel(
        mediaId: MediaId,
        private val lastFmService: LastFmService,
        getSongUseCase: GetSongUseCase

) : ViewModel(){

    private val displayedSong = MutableLiveData<DisplayableSong>()
    private lateinit var originalSong : Song
    private var getSongDisposable : Disposable? = null

    private var isFetching = false
    private var fetchSongInfoDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        getSongDisposable = getSongUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { this.originalSong = it }
                .map { it.toDisplayableSong() }
                .subscribe(displayedSong::postValue, Throwable::printStackTrace)
    }

    fun observeData(): LiveData<DisplayableSong> = displayedSong

    fun fetchSongInfo(){
        if (!isFetching){
            // todo check connection
            val song = this.originalSong
            fetchSongInfoDisposable = lastFmService.fetchSongInfo(song.title, song.artist)
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
        }
    }

    override fun onCleared() {
        getSongDisposable.unsubscribe()
        fetchSongInfoDisposable.unsubscribe()
    }

    private fun Song.toDisplayableSong(): DisplayableSong {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        val artist = if (this.artist == AppConstants.UNKNOWN) "" else this.artist
        val album = if (this.album == AppConstants.UNKNOWN) "" else this.album

        return DisplayableSong(
                this.id,
                this.title,
                artist,
                album,
                tag.getFirst(FieldKey.GENRE) ?: "",
                tag.getFirst(FieldKey.YEAR) ?: "",
                tag.getFirst(FieldKey.DISC_NO) ?: "",
                tag.getFirst(FieldKey.TRACK) ?: "",
                this.image
        )
    }

}