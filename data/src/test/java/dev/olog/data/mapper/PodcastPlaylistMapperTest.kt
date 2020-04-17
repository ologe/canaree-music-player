package dev.olog.data.mapper

import dev.olog.data.model.db.PlaylistEntity
import dev.olog.data.model.db.PodcastPlaylistEntity
import dev.olog.domain.entity.track.Playlist
import org.junit.Assert.assertEquals
import org.junit.Test

class PodcastPlaylistMapperTest {

    @Test
    fun testPodcastMapper() {
        // given
        val entity = PodcastPlaylistEntity(
            id = 1L,
            name = "playlist",
            size = 10
        )

        // when
        val actual = entity.toDomain()

        // then
        val expected = Playlist(
            id = 1L,
            title = "playlist",
            size = 10,
            isPodcast = true
        )

        assertEquals(expected, actual)
    }

    @Test
    fun testMapper() {
        // given
        val entity = PlaylistEntity(
            id = 1L,
            name = "playlist",
            size = 10
        )

        // when
        val actual = entity.toDomain()

        // then
        val expected = Playlist(
            id = 1L,
            title = "playlist",
            size = 10,
            isPodcast = false
        )

        assertEquals(expected, actual)
    }

}