package dev.olog.msc.presentation.library.tab

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dagger.Lazy
import dev.olog.msc.domain.entity.LibrarySortType
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.asLiveData
import io.reactivex.Observable
import javax.inject.Inject

class TabFragmentViewModel @Inject constructor(
        private val data: Lazy<Map<MediaIdCategory, Observable<List<DisplayableItem>>>>,
        private val appPreferencesUseCase: AppPreferencesUseCase

) : ViewModel() {

    private val liveDataList: MutableMap<MediaIdCategory, LiveData<List<DisplayableItem>>> = mutableMapOf()

    fun observeData(category: MediaIdCategory): LiveData<List<DisplayableItem>> {
        var liveData: LiveData<List<DisplayableItem>>? = liveDataList[category]
        if (liveData == null) {
            liveData = data.get()[category]!!.asLiveData()
            liveDataList[category] = liveData
        }

        return liveData
    }

    fun getAllTracksSortOrder(): LibrarySortType {
        return appPreferencesUseCase.getAllTracksSortOrder()
    }

    fun getAllAlbumsSortOrder(): LibrarySortType {
        return appPreferencesUseCase.getAllAlbumsSortOrder()
    }

    fun getAllArtistsSortOrder(): LibrarySortType {
        return appPreferencesUseCase.getAllArtistsSortOrder()
    }

//    fun observeAlbumSpanSize(category: MediaIdCategory): Observable<GridSpanSize> {
//        return appPreferencesUseCase.observeSpanSize(category)
//    }

}