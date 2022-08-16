package dev.olog.data.sort.db

import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.sort.SortTypeV2

const val SORT_TYPE_TITLE = "title"
const val SORT_TYPE_ARTIST = "artist"
const val SORT_TYPE_ALBUM = "album"
const val SORT_TYPE_ALBUM_ARTIST = "album_artist"
const val SORT_TYPE_DURATION = "duration"
const val SORT_TYPE_DATE = "date_added"
const val SORT_TYPE_TRACK_NUMBER = "track_number"
const val SORT_TYPE_CUSTOM = "custom"

enum class SortTypeEntity(val columnName: String) {
    Title(SORT_TYPE_TITLE),
    Artist(SORT_TYPE_ARTIST),
    Album(SORT_TYPE_ALBUM),
    AlbumArtist(SORT_TYPE_ALBUM_ARTIST),
    Duration(SORT_TYPE_DURATION),
    Date(SORT_TYPE_DATE),
    TrackNumber(SORT_TYPE_TRACK_NUMBER),
    Custom(SORT_TYPE_CUSTOM);

    companion object {
        operator fun invoke(type: SortTypeV2): SortTypeEntity {
            return when (type) {
                SortTypeV2.Title -> Title
                SortTypeV2.Artist -> Artist
                SortTypeV2.Album -> Album
                SortTypeV2.AlbumArtist -> AlbumArtist
                SortTypeV2.Duration -> Duration
                SortTypeV2.Date -> Date
                SortTypeV2.TrackNumber -> TrackNumber
                SortTypeV2.Custom -> Custom
            }
        }
        operator fun invoke(legacy: SortType): SortTypeEntity {
            return when (legacy) {
                SortType.TITLE -> Title
                SortType.ARTIST -> Artist
                SortType.ALBUM_ARTIST -> Album
                SortType.ALBUM -> AlbumArtist
                SortType.DURATION -> Duration
                SortType.RECENTLY_ADDED -> Date
                SortType.TRACK_NUMBER -> TrackNumber
                SortType.CUSTOM -> Custom
            }
        }
    }

    fun toDomain(): SortTypeV2 {
        return when (this) {
            Title -> SortTypeV2.Title
            Artist -> SortTypeV2.Artist
            Album -> SortTypeV2.Album
            AlbumArtist -> SortTypeV2.AlbumArtist
            Duration -> SortTypeV2.Duration
            Date -> SortTypeV2.Date
            TrackNumber -> SortTypeV2.TrackNumber
            Custom -> SortTypeV2.Custom
        }
    }

    override fun toString(): String = columnName

}