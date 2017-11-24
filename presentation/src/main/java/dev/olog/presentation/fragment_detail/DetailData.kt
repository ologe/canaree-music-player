package dev.olog.presentation.fragment_detail

import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem

class DetailData {

    enum class DataType {
        MOST_PLAYED, RECENT, ALBUMS, SONGS, ARTISTS_IN
    }

    private val mostPlayedData: List<DisplayableItem> = mutableListOf(
            DisplayableItem(R.layout.item_detail_alb, "media", ""),
            DisplayableItem(R.layout.item_shuffle, "media", ""),
            DisplayableItem(R.layout.a_prova, "media", "Most Played"),
            DisplayableItem(R.layout.item_detail_song, "media", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media1", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media2", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media3", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media4", "title", "artist")
    )
    private val recentlyAddedData: List<DisplayableItem> = mutableListOf(
            DisplayableItem(R.layout.a_prova, "media", "Recently Added"),
            DisplayableItem(R.layout.item_detail_song, "media5", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media6", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media7", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media8", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media9", "title", "artist")
    )
    private val albumsData: List<DisplayableItem> = mutableListOf(
            DisplayableItem(R.layout.a_prova, "media", "Albums"),
            DisplayableItem(R.layout.item_tab_album, "media10", "title"),
            DisplayableItem(R.layout.item_tab_album, "media11", "title"),
            DisplayableItem(R.layout.item_tab_album, "media12", "title"),
            DisplayableItem(R.layout.item_tab_album, "media13", "title"),
            DisplayableItem(R.layout.item_tab_album, "media14", "title")
    )
    private val songsData: List<DisplayableItem> = mutableListOf(
            DisplayableItem(R.layout.a_prova, "media", "Songs"),
            DisplayableItem(R.layout.item_detail_song, "media15", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media16", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media17", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media18", "title", "artist"),
            DisplayableItem(R.layout.item_detail_song, "media19", "title", "artist")
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