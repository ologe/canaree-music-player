package dev.olog.presentation.fragment_tab

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.domain.interactor.tab.InsertLastPlayedAlbumUseCase
import dev.olog.domain.interactor.tab.InsertLastPlayedArtistUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared_android.entity.TabCategory
import dev.olog.shared_android.extension.asLiveData
import io.reactivex.Completable
import io.reactivex.Flowable

class TabFragmentViewModel constructor(
        private val data: Map<TabCategory, Flowable<List<DisplayableItem>>>,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getArtistUseCase: GetArtistUseCase

) : ViewModel() {

    private val liveDataList: MutableMap<TabCategory, LiveData<List<DisplayableItem>>> = mutableMapOf()

    fun observeData(category: TabCategory): LiveData<List<DisplayableItem>> {
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