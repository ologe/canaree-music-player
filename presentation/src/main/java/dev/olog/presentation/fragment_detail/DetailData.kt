package dev.olog.presentation.fragment_detail

import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem

class DetailData {

    enum class DataType {
        MOST_PLAYED, RECENT, ALBUMS, SONGS, ARTISTS_IN
    }

    private val mostPlayedData: List<DisplayableItem> = mutableListOf(
//            DisplayableItem(R.layout.item_filter, "media", ""),
            DisplayableItem(R.layout.item_detail_info, "media", ""),
//            DisplayableItem(R.layout.item_shuffle, "media", ""),
            DisplayableItem(R.layout.item_header, "media", "Most Played"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - 4 A.M ft. Travis Scott", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Good Drank feat Gucci Mane", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - It's a Vibe", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Lil Baby", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - OG Kush Diet", "Unkown Artist")
    )
    private val recentlyAddedData: List<DisplayableItem> = mutableListOf(
            DisplayableItem(R.layout.item_header, "media", "Recently Added"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - 4 A.M ft. Travis Scott", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Good Drank feat Gucci Mane", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - It's a Vibe", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Lil Baby", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - OG Kush Diet", "Unkown Artist")
    )
    private val albumsData: List<DisplayableItem> = mutableListOf(
            DisplayableItem(R.layout.item_header, "media", "Albums"),
            DisplayableItem(R.layout.item_detail_album, "media10", "...Like Clockwork"),
            DisplayableItem(R.layout.item_detail_album, "media11", "2001"),
            DisplayableItem(R.layout.item_detail_album, "media12", "2014 Forest Hills Drive")
    )
    private val songsData: List<DisplayableItem> = mutableListOf(
            DisplayableItem(R.layout.item_header, "media", "Songs"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - 4 A.M ft. Travis Scott", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Good Drank feat Gucci Mane", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - It's a Vibe", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - Lil Baby", "Unkown Artist"),
            DisplayableItem(R.layout.item_detail_song, "media", "2 Chainz - OG Kush Diet", "Unkown Artist")
    )
    private val artistsInData: List<DisplayableItem> = mutableListOf()

    private val data : Map<DataType, List<DisplayableItem>> = mutableMapOf(
            DataType.MOST_PLAYED to mostPlayedData,
            DataType.RECENT to recentlyAddedData,
            DataType.ALBUMS to albumsData,
            DataType.SONGS to songsData,
            DataType.ARTISTS_IN to artistsInData
    )

    fun getSize(): Int = data.values.sumBy { it.size }

    operator fun get(position: Int): DisplayableItem {
        var totalSize = 0
        for (value in data.values) {
            if (position in totalSize until (totalSize + value.size)){
                val realPosition = position - totalSize
                return value[realPosition]
            } else{
                totalSize += value.size
            }
        }
        throw IllegalArgumentException("invalid position $position")
    }

}