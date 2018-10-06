package dev.olog.msc.presentation.edit.album

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import org.jaudiotagger.tag.TagOptionSingleton
import javax.inject.Inject

class EditAlbumFragmentViewModel @Inject constructor(
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
                    displayedAlbum.postValue(it)
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

    fun getAlbum(): DisplayableAlbum = presenter.getAlbum()

    fun observeData(): LiveData<DisplayableAlbum> = displayedAlbum

    fun observeSongList(): LiveData<List<Song>> = songListLiveData



    override fun onCleared() {
        songListDisposable.unsubscribe()
    }

}