package dev.olog.msc.presentation.library.tab

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dagger.Lazy
import dev.olog.msc.domain.interactor.all.last.played.InsertLastPlayedAlbumUseCase
import dev.olog.msc.domain.interactor.all.last.played.InsertLastPlayedArtistUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.asLiveData
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class TabFragmentViewModel constructor(
        private val data: Lazy<Map<MediaIdCategory, Observable<List<DisplayableItem>>>>,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase

) : ViewModel() {

    private val liveDataList: MutableMap<MediaIdCategory, LiveData<List<DisplayableItem>>> = mutableMapOf()

    private val subscriptions = CompositeDisposable()

    fun observeData(category: MediaIdCategory): LiveData<List<DisplayableItem>> {
        var liveData: LiveData<List<DisplayableItem>>? = liveDataList[category]
        if (liveData == null) {
            liveData = data.get()[category]!!.asLiveData()
            liveDataList[category] = liveData
        }

        return liveData
    }

    fun insertLastPlayed(mediaId: MediaId){
        val id = mediaId.resolveId
        when (mediaId.category) {
            MediaIdCategory.ARTISTS -> insertLastPlayedArtistUseCase.execute(id)
            MediaIdCategory.ALBUMS -> insertLastPlayedAlbumUseCase.execute(id)
            else -> Completable.complete()
        }.subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    override fun onCleared() {
        subscriptions.clear()
    }

}