package dev.olog.msc.presentation.library.tab

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.msc.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.msc.domain.interactor.tab.InsertLastPlayedAlbumUseCase
import dev.olog.msc.domain.interactor.tab.InsertLastPlayedArtistUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.shared_android.extension.asLiveData
import io.reactivex.Completable
import io.reactivex.Flowable

class TabFragmentViewModel constructor(
        private val data: Map<MediaIdCategory, Flowable<List<DisplayableItem>>>,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getArtistUseCase: GetArtistUseCase

) : ViewModel() {

    private val liveDataList: MutableMap<MediaIdCategory, LiveData<List<DisplayableItem>>> = mutableMapOf()

    fun observeData(category: MediaIdCategory): LiveData<List<DisplayableItem>> {
        var liveData: LiveData<List<DisplayableItem>>? = liveDataList[category]
        if (liveData == null) {
            liveData = data[category]!!.asLiveData()
            liveDataList[category] = liveData
        }

        return liveData
    }

    fun insertAlbumLastPlayed(mediaId: MediaId): Completable{
        return getAlbumUseCase.execute(mediaId)
                .firstOrError()
                .flatMapCompletable { insertLastPlayedAlbumUseCase.execute(it) }
    }

    fun insertArtistLastPlayed(mediaId: MediaId): Completable{
        return getArtistUseCase.execute(mediaId)
                .firstOrError()
                .flatMapCompletable { insertLastPlayedArtistUseCase.execute(it) }
    }


}