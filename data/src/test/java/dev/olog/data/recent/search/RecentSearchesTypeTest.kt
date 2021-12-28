package dev.olog.data.recent.search

import dev.olog.core.MediaIdCategory
import org.junit.Assert
import org.junit.Test

class RecentSearchesTypeTest {

    @Test
    fun test() {
        val map = mapOf(
            MediaIdCategory.FOLDERS to 0,
            MediaIdCategory.PLAYLISTS to 1,
            MediaIdCategory.SONGS to 2,
            MediaIdCategory.ALBUMS to 3,
            MediaIdCategory.ARTISTS to 4,
            MediaIdCategory.GENRES to 5,
            MediaIdCategory.PODCASTS_PLAYLIST to 6,
            MediaIdCategory.PODCASTS to 7,
            MediaIdCategory.PODCASTS_ALBUMS to 8,
            MediaIdCategory.PODCASTS_ARTISTS to 9,
        )

        for (item in MediaIdCategory.values()) {
            Assert.assertEquals(map[item], item.recentSearchType())
        }
    }

}