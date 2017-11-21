package dev.olog.presentation.fragment_tab

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.util.SparseArray
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.asLiveData
import io.reactivex.Flowable

class TabViewModel constructor(
        private val data: Map<Int, Flowable<List<DisplayableItem>>>
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

}