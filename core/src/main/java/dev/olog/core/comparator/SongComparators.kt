package dev.olog.core.comparator

import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.Song

object SongComparators {

    operator fun invoke(sort: SortEntity): Comparator<Song> {
        val comparator: Comparator<Song> = when (sort.type) {
            SortType.TITLE -> CanareeCollator.compareBy({ it.title })
            SortType.ARTIST -> CanareeCollator.compareBy({ it.artist }, { it.title })
            SortType.ALBUM_ARTIST -> CanareeCollator.compareBy({ it.albumArtist }, { it.title })
            SortType.ALBUM -> CanareeCollator.compareBy({ it.album }, { it.title })
            SortType.DURATION -> CanareeCollator.compareBy({ it.duration })
            SortType.RECENTLY_ADDED -> CanareeCollator.compareByDescending({ it.dateAdded })
            SortType.TRACK_NUMBER -> CanareeCollator.compareBy({ it.discNumber }, { it.trackNumber }, { it.title })
            SortType.CUSTOM -> CanareeCollator.compareBy({ it.title })
        }
        return when (sort.arranging) {
            SortArranging.ASCENDING -> comparator
            SortArranging.DESCENDING -> comparator.reversed()
        }
    }

}