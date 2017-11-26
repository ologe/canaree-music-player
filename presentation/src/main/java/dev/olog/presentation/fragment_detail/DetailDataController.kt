package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor

class DetailDataController(
        context: Context,
        private val adapter: DetailAdapter,
        source: Int

) : DefaultLifecycleObserver {

    private val seeAll = context.getString(R.string.detail_see_all)

    private var dataSetDisposable: Disposable? = null

    val publisher :PublishProcessor<Pair<DetailDataType, List<DisplayableItem>>> =
            PublishProcessor.create<Pair<DetailDataType ,List<DisplayableItem>>>()

    // header
    private val headerData : List<DisplayableItem> = mutableListOf()

    // most played
    private val mostPlayedHeader = DisplayableItem(R.layout.item_header, "most played id", context.getString(R.string.detail_most_played))
    private val mostPlayedList = DisplayableItem(R.layout.item_most_played_horizontal_list, "most played list", "")
    private val mostPlayedData: List<DisplayableItem> = mutableListOf(mostPlayedHeader,
            DisplayableItem(R.layout.item_most_played_horizontal_list, "media", "Most Played"))

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
    private val songsData: List<DisplayableItem> = mutableListOf()

    // artist in this data
    private val artistsInData: List<DisplayableItem> = mutableListOf()

    val fakeData: List<DisplayableItem> = mutableListOf(
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - 4 A.M ft. Travis Scott", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Good Drank feat Gucci Mane", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - It's a Vibe", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Lil Baby", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - OG Kush Diet", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - 4 A.M ft. Travis Scott", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Good Drank feat Gucci Mane", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - It's a Vibe", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Lil Baby", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - OG Kush Diet", "Unkown Artist")
    )

    private val dataSet : MutableMap<DetailDataType, List<DisplayableItem>> = mutableMapOf(
            DetailDataType.HEADER to headerData,
            DetailDataType.MOST_PLAYED to mostPlayedData,
            DetailDataType.RECENT to recentlyAddedData,
            DetailDataType.ALBUMS to albumsData,
            DetailDataType.ARTISTS_IN to artistsInData,
            DetailDataType.SONGS to songsData
    )

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
                .onBackpressureBuffer()
                .subscribe { (type, data) ->
                    dataSet[type] = addHeaderByType(type, data)
                    adapter.notifyDataSetChanged()
                }
    }

    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    private fun addHeaderByType(type: DetailDataType, list: List<DisplayableItem>) : MutableList<DisplayableItem> {
        var result : MutableList<DisplayableItem> = list.toMutableList()
        when (type){
            DetailDataType.HEADER -> {
            }
            DetailDataType.MOST_PLAYED -> {
                if (result.isNotEmpty()){
                    result.clear() // all list is not needed, just add a nested list
                    result.add(0, mostPlayedHeader)
                    result.add(1, mostPlayedList)
                }
            }
            DetailDataType.RECENT -> {
                if (result.isNotEmpty()){
                    result.clear() // all list is not needed, just add a nested list
                    result.add(0, recentlyAddedHeader.copy(subtitle =
                            if (list.size > 10) seeAll else ""))
                    result.add(1, recentlyAddedList)
                }
            }
            DetailDataType.ALBUMS -> {
                result = result.take(4).toMutableList()
                result.add(0, albumsHeader.copy(subtitle =
                        if (list.size > 4) seeAll else ""))
            }
            DetailDataType.SONGS -> {
                result.add(0, songsHeader)
            }
        }
        return result
    }

}