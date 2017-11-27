package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v7.util.DiffUtil
import dev.olog.presentation.DetailDiff
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class DetailDataController(
        context: Context,
        private val adapter: DetailAdapter,
        source: Int

) : DefaultLifecycleObserver {

    private val seeAll = context.getString(R.string.detail_see_all)

    private var dataSetDisposable: Disposable? = null

    private val publisher = PublishProcessor.create<Map<DetailDataType, List<DisplayableItem>>>()

    // header
    private val headerData : List<DisplayableItem> = mutableListOf()

    // most played
    private val mostPlayedHeader = DisplayableItem(R.layout.item_header, "most played id", context.getString(R.string.detail_most_played))
    private val mostPlayedList = DisplayableItem(R.layout.item_most_played_horizontal_list, "most played list", "")
    private val mostPlayedData: List<DisplayableItem> = mutableListOf()

    // recent
    private val recentlyAddedHeader = DisplayableItem(R.layout.item_header, "recent id", context.getString(R.string.detail_recently_added))
    private val recentlyAddedList = DisplayableItem(R.layout.item_recent_horizontal_list, "recent list", "")
    private val recentlyAddedData: List<DisplayableItem> = mutableListOf()

    // albums
    private val albumsHeader = DisplayableItem(R.layout.item_header, "albums id",
            context.resources.getStringArray(R.array.detail_album_header)[source])
    private val albumsData: List<DisplayableItem> = mutableListOf()

    // songs
    private val songsHeader = DisplayableItem(R.layout.item_header, "songs id", context.getString(R.string.detail_songs))
    private val shuffleHeader = DisplayableItem(R.layout.item_shuffle_with_divider, "shuffle id", "")
    private val songsData: List<DisplayableItem> = mutableListOf()

    // artist in this data
    private val artistsInData: List<DisplayableItem> = mutableListOf()

    private val originalDataSet : MutableMap<DetailDataType, List<DisplayableItem>> = mutableMapOf(
            DetailDataType.HEADER to headerData,
            DetailDataType.MOST_PLAYED to mostPlayedData,
            DetailDataType.RECENT to recentlyAddedData,
            DetailDataType.ALBUMS to albumsData,
            DetailDataType.ARTISTS_IN to artistsInData,
            DetailDataType.SONGS to songsData
    )

    private val dataSet : MutableMap<DetailDataType, List<DisplayableItem>> = originalDataSet.toMutableMap()

    fun getSize(): Int = dataSet.values.sumBy { it.size }

    operator fun get(position: Int): DisplayableItem {
        var totalSize = 0
        for (value in dataSet.values) {
            if (position in totalSize until (totalSize + value.size)){
                val realPosition = position - totalSize
                return value[realPosition]
            } else{
                totalSize += value.size
            }
        }
        throw IllegalArgumentException("invalid position $position")
    }

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = publisher
                .toSerialized()
                .debounce(50, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .onBackpressureBuffer()
                .distinctUntilChanged()
                .map { it.to(DiffUtil.calculateDiff(DetailDiff(dataSet, it))) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (newData, callback) ->
                    val wasEmpty = isEmpty()
                    dataSet.clear()
                    dataSet.putAll(newData)
                    if (wasEmpty){
                        adapter.notifyDataSetChanged()
                    } else{
                        callback.dispatchUpdatesTo(adapter)
                    }
                    adapter.startTransition()
                }
    }

    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    fun onNext(data :Map<DetailDataType, MutableList<DisplayableItem>>){
        originalDataSet.clear()
        originalDataSet.putAll(addHeaderByType(data))
        publisher.onNext(originalDataSet.toMap())
    }

    private fun isEmpty(): Boolean = getSize() == 0

    private fun addHeaderByType(data :Map<DetailDataType, MutableList<DisplayableItem>>) : Map<DetailDataType, List<DisplayableItem>> {

        for ((key, value) in data.entries) {
            when (key){
                DetailDataType.MOST_PLAYED -> {
                    if (value.isNotEmpty()){
                        value.clear() // all list is not needed, just add a nested list
                        value.add(0, mostPlayedHeader)
                        value.add(1, mostPlayedList)
                    }
                }
                DetailDataType.RECENT -> {
                    if (value.isNotEmpty()){
                        value.clear() // all list is not needed, just add a nested list
                        value.add(0, recentlyAddedHeader.copy(subtitle =
                        if (value.size > 10) seeAll else ""))
                        value.add(1, recentlyAddedList)
                    }
                }
                DetailDataType.ALBUMS -> {
                    if (value.isNotEmpty()){
                        val newList = value.take(4).toMutableList()
                        val header = value[0]
                        if (header.mediaId != albumsHeader.mediaId){
                            newList.add(0, albumsHeader.copy(subtitle =
                            if (value.size > 4) seeAll else ""))
                        }
                        value.clear()
                        value.addAll(newList)
                    }
                }
                DetailDataType.SONGS -> {
                    if (value.isNotEmpty()){
                        val header = value[0]
                        if (header != songsHeader){
                            value.add(0, songsHeader)
                            value.add(1, shuffleHeader)
                        }
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