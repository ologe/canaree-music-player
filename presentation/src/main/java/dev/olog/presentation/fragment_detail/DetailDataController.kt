package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.DefaultLifecycleObserver
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.cleanThenAdd

class DetailDataController (
        private val adapter: DetailAdapter

) : DefaultLifecycleObserver {

    lateinit var detailHeaders : DetailHeaders

    private fun addHeaderByType(data :MutableMap<DetailDataType, MutableList<DisplayableItem>>)
            : MutableMap<DetailDataType, MutableList<DisplayableItem>> {

        for ((key, value) in data.entries) {
            when (key){
                DetailDataType.MOST_PLAYED -> {
                    if (value.isNotEmpty()){
                        value.clear() // all list is not needed, just add a nested list
                        value.addAll(0, detailHeaders.mostPlayed)
                    }
                }
                DetailDataType.RECENT -> {
                    if (value.isNotEmpty()){
                        value.clear() // all list is not needed, just add a nested list
                        if (value.size > 10){
                            value.addAll(0, detailHeaders.recentWithSeeAll)
                        } else {
                            value.addAll(0, detailHeaders.recent)
                        }
                    }
                }
                DetailDataType.ALBUMS -> {
                    if (value.isNotEmpty()) {
                        val newList = value.take(4).toMutableList()
                        if (value.size > 4){
                            newList.add(0, detailHeaders.albumsWithSeeAll)
                        } else{
                            newList.add(0, detailHeaders.albums)
                        }
                        value.cleanThenAdd(newList)
                    }
                }
                DetailDataType.SONGS -> {
                    if (value.isNotEmpty()){
                        value.addAll(0, detailHeaders.songs)
                    }
                }
                DetailDataType.ARTISTS_IN -> {
                    if (value.isNotEmpty()){
                        val (_, _, title) = value[0]
                        if (title == ""){
                            value.clear()
                        }
                    }
                }
            }
        }

        return data
    }

}