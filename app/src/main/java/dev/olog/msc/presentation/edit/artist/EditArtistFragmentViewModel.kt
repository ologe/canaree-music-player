package dev.olog.msc.presentation.edit.artist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import org.jaudiotagger.tag.TagOptionSingleton

class EditArtistFragmentViewModel(
        private val presenter: EditArtistFragmentPresenter

) : ViewModel(){

    private val songList = MutableLiveData<List<Song>>()

    private val displayedArtist = MutableLiveData<DisplayableArtist>()

    private var songListDisposable: Disposable? = null
    private var artistDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        artistDisposable = presenter.observeArtist()
                .subscribe({
                    this.displayedArtist.postValue(it.toDisplayableArtist())
                }, Throwable::printStackTrace)

        songListDisposable = presenter.getSongList()
                .subscribe({
                    songList.postValue(it)
                }, Throwable::printStackTrace)
    }

    fun updateImage(image: String?){
        val oldValue = displayedArtist.value!!
        val newValue = oldValue.copy(image = image)
        displayedArtist.postValue(newValue)
    }

    fun getNewImage(): String? {
        return displayedArtist.value!!.image

    }

    fun getAlbum(): Artist = presenter.getAlbum()

    override fun onCleared() {
        songListDisposable.unsubscribe()
    }

    fun observeData(): LiveData<DisplayableArtist> = displayedArtist
    fun observeSongList(): LiveData<List<Song>> = songList

    private fun Artist.toDisplayableArtist(): DisplayableArtist {
        return DisplayableArtist(
                this.id,
                this.name,
                this.image
        )
    }

}