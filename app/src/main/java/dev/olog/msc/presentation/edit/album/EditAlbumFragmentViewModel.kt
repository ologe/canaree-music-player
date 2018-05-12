package dev.olog.msc.presentation.edit.album

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.get
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

class EditAlbumFragmentViewModel(
        private val presenter: EditAlbumFragmentPresenter

) : ViewModel() {

    private val songListLiveData = MutableLiveData<List<Song>>()

    private val displayedAlbum = MutableLiveData<DisplayableAlbum>()

    private var songListDisposable: Disposable? = null
    private var albumDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        albumDisposable = presenter.observeAlbum()
                .subscribe({
                    displayedAlbum.postValue(it.first.toDisplayableAlbum(it.second))
                }, Throwable::printStackTrace)

        songListDisposable = presenter.getSongList()
                .subscribe({
                    songListLiveData.postValue(it)
                }, Throwable::printStackTrace)
    }

    fun updateImage(image: String){
        val oldValue = displayedAlbum.value!!
        val newValue = oldValue.copy(image = image)
        displayedAlbum.postValue(newValue)
    }

    fun getNewImage(): String? {
        val albumId = getAlbum().id
        val original = ImagesFolderUtils.forAlbum(albumId)
        val current = displayedAlbum.value!!.image
        if (original == current){
            return null
        } else {
            return current
        }
    }

    fun getAlbum(): Album = presenter.getAlbum()

    fun observeData(): LiveData<DisplayableAlbum> = displayedAlbum

    fun observeSongList(): LiveData<List<Song>> = songListLiveData

    private fun Album.toDisplayableAlbum(path: String): DisplayableAlbum {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableAlbum(
                this.id,
                this.title,
                DisplayableItem.adjustArtist(this.artist),
                tag.get(FieldKey.GENRE),
                tag.get(FieldKey.YEAR),
                this.image
        )
    }

    override fun onCleared() {
        songListDisposable.unsubscribe()
    }

}