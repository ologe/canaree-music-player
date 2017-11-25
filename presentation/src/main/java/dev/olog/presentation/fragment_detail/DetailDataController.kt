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
        private val source: Int

) : DefaultLifecycleObserver {

    private var dataSetDisposable: Disposable? = null
    val publisher :PublishProcessor<Pair<DataType, List<DisplayableItem>>> =
            PublishProcessor.create<Pair<DataType ,List<DisplayableItem>>>()

    enum class DataType {
        HEADER, MOST_PLAYED, RECENT, ALBUMS, SONGS, ARTISTS_IN
    }

    private val mostPlayedHeader = DisplayableItem(R.layout.item_header, "most played id",
            context.getString(R.string.detail_most_played))
    private val recentlyAddedHeader = DisplayableItem(R.layout.item_header, "recent id",
            context.getString(R.string.detail_recently_added))
    private val albumsHeader = DisplayableItem(R.layout.item_header, "albums id",
            context.resources.getStringArray(R.array.detail_album_header)[source])
    private val songsHeader = DisplayableItem(R.layout.item_header, "songs id",
            context.getString(R.string.detail_songs))

    private val headerData : List<DisplayableItem> = mutableListOf()

    private val mostPlayedData: List<DisplayableItem> = mutableListOf(
            mostPlayedHeader,
            DisplayableItem(R.layout.item_most_played_horizontal_list, "media", "Most Played")
    )
    private val recentlyAddedData: List<DisplayableItem> = mutableListOf(
            recentlyAddedHeader,
            DisplayableItem(R.layout.item_recent_horizontal_list, "media", "Recently Added")
    )

    private val albumsData: List<DisplayableItem> = mutableListOf()
    private val songsData: List<DisplayableItem> = mutableListOf()

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


    private val artistsInData: List<DisplayableItem> = mutableListOf()

    private val dataSet : MutableMap<DataType, List<DisplayableItem>> = mutableMapOf(
            DataType.HEADER to headerData,
            DataType.MOST_PLAYED to mostPlayedData,
            DataType.RECENT to recentlyAddedData,
            DataType.ALBUMS to albumsData,
            DataType.SONGS to songsData,
            DataType.ARTISTS_IN to artistsInData
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
                .subscribe { (type, data) ->
                    val asMutable = data.toMutableList()
                    addHeaderByType(type, asMutable)
                    dataSet[type] = asMutable
                    adapter.notifyDataSetChanged()
                }
    }

    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    private fun addHeaderByType(type: DataType, list: MutableList<DisplayableItem>) {
        when (type){
            DataType.MOST_PLAYED -> list.add(0,mostPlayedHeader)
            DataType.RECENT -> list.add(0,recentlyAddedHeader)
            DataType.ALBUMS -> list.add(0,albumsHeader)
            DataType.SONGS -> list.add(0,songsHeader)
        }
    }

}