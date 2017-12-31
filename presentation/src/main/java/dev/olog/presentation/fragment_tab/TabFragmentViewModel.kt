package dev.olog.presentation.fragment_tab

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.util.SparseArray
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.domain.interactor.tab.InsertLastPlayedAlbumUseCase
import dev.olog.domain.interactor.tab.InsertLastPlayedArtistUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaId
import io.reactivex.Completable
import io.reactivex.Flowable

class TabFragmentViewModel constructor(
        private val data: Map<Int, Flowable<List<DisplayableItem>>>,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getArtistUseCase: GetArtistUseCase

) : ViewModel() {

    private val liveDataList: SparseArray<LiveData<List<DisplayableItem>>> = SparseArray(10)

    fun observeData(tabPosition: Int): LiveData<List<DisplayableItem>> {
        var liveData: LiveData<List<DisplayableItem>>? = liveDataList.get(tabPosition)
        if (liveData == null) {
            liveData = data[tabPosition]!!.asLiveData()
            liveDataList.put(tabPosition, liveData)
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