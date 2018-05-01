package dev.olog.msc.utils

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.music.service.model.MediaEntity

object ComparatorUtils {

    fun getAscendingTrackNumberComparator(): Comparator<Song> {
        return Comparator { o1: Song, o2: Song ->
            val tmp = o1.discNumber - o2.discNumber
            if (tmp == 0){
                o1.trackNumber - o2.trackNumber
            } else {
                tmp
            }
        }
    }

    fun getDescendingTrackNumberComparator(): Comparator<Song> {
        return Comparator { o1: Song, o2: Song ->
            val tmp = o2.discNumber - o1.discNumber
            if (tmp == 0){
                o2.trackNumber - o1.trackNumber
            } else {
                tmp
            }
        }
    }

    fun getMediaEntityAscendingTrackNumberComparator(): Comparator<MediaEntity> {
        return Comparator { o1: MediaEntity, o2: MediaEntity ->
            val tmp = o1.discNumber - o2.discNumber
            if (tmp == 0){
                o1.trackNumber - o2.trackNumber
            } else {
                tmp
            }
        }
    }

    fun getMediaEntityDescendingTrackNumberComparator(): Comparator<MediaEntity> {
        return Comparator { o1: MediaEntity, o2: MediaEntity ->
            val tmp = o2.discNumber - o1.discNumber
            if (tmp == 0){
                o2.trackNumber - o1.trackNumber
            } else {
                tmp
            }
        }
    }

}