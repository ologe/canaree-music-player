package dev.olog.service.music.utils

import dev.olog.core.entity.track.Song

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

    fun getMediaEntityAscendingTrackNumberComparator(): Comparator<dev.olog.service.music.model.MediaEntity> {
        return Comparator { o1: dev.olog.service.music.model.MediaEntity, o2: dev.olog.service.music.model.MediaEntity ->
            val tmp = o1.discNumber - o2.discNumber
            if (tmp == 0){
                o1.trackNumber - o2.trackNumber
            } else {
                tmp
            }
        }
    }

    fun getMediaEntityDescendingTrackNumberComparator(): Comparator<dev.olog.service.music.model.MediaEntity> {
        return Comparator { o1: dev.olog.service.music.model.MediaEntity, o2: dev.olog.service.music.model.MediaEntity ->
            val tmp = o2.discNumber - o1.discNumber
            if (tmp == 0){
                o2.trackNumber - o1.trackNumber
            } else {
                tmp
            }
        }
    }

}