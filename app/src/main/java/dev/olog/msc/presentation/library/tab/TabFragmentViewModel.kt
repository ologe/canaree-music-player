package dev.olog.msc.presentation.library.tab

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.interactor.tab.InsertLastPlayedAlbumUseCase
import dev.olog.msc.domain.interactor.tab.InsertLastPlayedArtistUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.asLiveData
import io.reactivex.Completable
import io.reactivex.Observable

class TabFragmentViewModel constructor(
        private val data: Map<MediaIdCategory, Observable<List<DisplayableItem>>>,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase

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
        val albumId = mediaId.categoryValue.toLong()
        return insertLastPlayedAlbumUseCase.execute(albumId)
    }

    fun insertArtistLastPlayed(mediaId: MediaId): Completable{
        val artistId = mediaId.categoryValue.toLong()
        return insertLastPlayedArtistUseCase.execute(artistId)
    }


}