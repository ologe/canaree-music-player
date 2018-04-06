package dev.olog.msc.presentation.edit.album

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import androidx.core.text.isDigitsOnly
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.context
import dev.olog.msc.utils.k.extension.get
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.msc.utils.media.store.notifyMediaStore
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

class EditAlbumFragmentViewModel(
        application: Application,
        private val presenter: EditAlbumFragmentPresenter

) : AndroidViewModel(application) {

    private val songList = MutableLiveData<List<Song>>()

    private val displayedAlbum = MutableLiveData<DisplayableAlbum>()

    private var songListDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        songListDisposable = presenter.getSongList()
                .subscribe({
                    if (it.isNotEmpty()){
                        displayedAlbum.postValue(it[0].toDisplayableAlbum())
                    }
                    songList.postValue(it)
                }, Throwable::printStackTrace)
    }

    override fun onCleared() {
        songListDisposable.unsubscribe()
    }

    fun observeData(): LiveData<DisplayableAlbum> = displayedAlbum

    fun observeSongList(): LiveData<List<Song>> = songList

    fun updateMetadata(
            album: String,
            artist: String,
            genre: String,
            year: String

    ) : UpdateResult {

        when {
            album.isBlank() -> return UpdateResult.EMPTY_TITLE
            year.isNotBlank() && !year.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
        }

        try {
            presenter.updateSongList(album, artist, genre, year)
            for (song in presenter.songList) {
                notifyMediaStore(context, song.path)
            }

            return UpdateResult.OK
        } catch (ex: Exception){
            ex.printStackTrace()
            return UpdateResult.ERROR
        }
    }

    private fun Song.toDisplayableAlbum(): DisplayableAlbum {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableAlbum(
                this.albumId,
                this.album,
                DisplayableItem.adjustArtist(this.artist),
                tag.get(FieldKey.GENRE),
                tag.get(FieldKey.YEAR),
                this.image
        )
    }

}