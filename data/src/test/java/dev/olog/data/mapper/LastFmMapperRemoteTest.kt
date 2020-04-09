package dev.olog.lib.mapper

import com.google.gson.Gson
import dev.olog.lib.model.lastfm.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LastFmMapperRemoteTest {

    private val gson = Gson()

    @Test
    fun testTrackInfo() {
        // given
        val id = 1L
        val data = gson.fromJson(
            LastFmRemoteMockResult.trackInfo,
            LastFmTrackInfo::class.java
        )

        // when
        val item = data.toDomain(id)

        // then
        assertEquals(id, item.id)
        assertEquals("Believe", item.title)
        assertEquals("Cher", item.artist)
        assertEquals("Believe", item.album)

        assertEquals(
            "https://lastfm.freetls.fastly.net/i/u/300x300/3b54885952161aaea4ce2965b2db1638.png",
            item.image
        )

        assertEquals("32ca187e-ee25-4f18-b7d0-3b6713f24635", item.mbid)
        assertEquals("bfcc6d75-a6a5-4bc6-8282-47aec8531818", item.artistMbid)
        assertEquals("63b3a8ca-26f2-4e2b-b867-647a6ec2bebd", item.albumMbid)
    }

    @Test
    fun testTrackSearch() {
        // given
        val id = 1L
        val data = gson.fromJson(
            LastFmRemoteMockResult.trackSearch,
            LastFmTrackSearch::class.java
        )

        // when
        // TODO not working very well, should be `cher` not `imagine dragons` (second result)
        val item = data.toDomain(id)!!

        // then
        assertEquals(id, item.id)
        assertEquals("Believer", item.title)
        assertEquals("Imagine Dragons", item.artist)
        assertEquals("", item.album)

        assertEquals("", item.image)

        assertEquals("", item.mbid)
        assertEquals("", item.artistMbid)
        assertEquals("", item.albumMbid)
    }

    @Test
    fun testAlbumInfo() {
        // given
        val id = 1L
        val data = gson.fromJson(
            LastFmRemoteMockResult.albumInfo,
            LastFmAlbumInfo::class.java
        )

        // when
        val item = data.toDomain(id)

        // then
        assertEquals(id, item.id)
        assertEquals("Believe", item.title)
        assertEquals("Cher", item.artist)
        assertEquals(
            "https://lastfm.freetls.fastly.net/i/u/300x300/3b54885952161aaea4ce2965b2db1638.png",
            item.image
        )
        assertEquals("63b3a8ca-26f2-4e2b-b867-647a6ec2bebd", item.mbid)
        assertTrue(item.wiki.isNotBlank())
    }

    @Test
    fun testAlbumSearch() {
        // given
        val id = 1L
        val artist = "cher"
        val data = gson.fromJson(
            LastFmRemoteMockResult.albumSearch,
            LastFmAlbumSearch::class.java
        )

        // when
        val item = data.toDomain(id, artist)

        // then
        assertEquals(id, item.id)
        assertEquals("Believe", item.title)
        assertEquals("Cher", item.artist)
        assertEquals("", item.image)
        assertEquals("", item.mbid)
        assertTrue(item.wiki.isBlank())
    }

    @Test
    fun testArtistInfo() {
        val id = 1L
        val data = gson.fromJson(
            LastFmRemoteMockResult.artistInfo,
            LastFmArtistInfo::class.java
        )

        // when
        val item = data.toDomain(id)!!

        // then
        assertEquals(id, item.id)
        assertEquals("", item.image)
        assertEquals("bfcc6d75-a6a5-4bc6-8282-47aec8531818", item.mbid)
        assertTrue(item.wiki.isNotBlank())
    }

}