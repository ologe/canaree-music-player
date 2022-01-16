package dev.olog.core

import org.junit.Assert
import org.junit.Test

class MediaUriTest {

    @Test
    fun `test song`() {
        val uri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = "id",
            isPodcast = false,
        )

        Assert.assertEquals(MediaUri.Source.MediaStore, uri.source)
        Assert.assertEquals(MediaUri.Category.Track, uri.category)
        Assert.assertEquals("id", uri.id)
        Assert.assertEquals(false, uri.isPodcast)
        Assert.assertEquals(null, uri.modifier)
        Assert.assertEquals("mediastore:track:id", uri.toString())
        Assert.assertEquals(listOf("mediastore", "track", "id"), uri.schema)
        Assert.assertEquals(null, uri.query)
    }

    @Test
    fun `test podcast`() {
        val uri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = "id",
            isPodcast = true,
        )

        Assert.assertEquals(MediaUri.Source.MediaStore, uri.source)
        Assert.assertEquals(MediaUri.Category.Track, uri.category)
        Assert.assertEquals("id", uri.id)
        Assert.assertEquals(true, uri.isPodcast)
        Assert.assertEquals(null, uri.modifier)
        Assert.assertEquals("mediastore:track:id?podcast", uri.toString())
        Assert.assertEquals(listOf("mediastore", "track", "id"), uri.schema)
        Assert.assertEquals("podcast", uri.query)
    }

    @Test
    fun `test song with modifier`() {
        val uri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = "id",
            isPodcast = false,
            modifier = MediaUri.Modifier.MostPlayed,
        )

        Assert.assertEquals(MediaUri.Source.MediaStore, uri.source)
        Assert.assertEquals(MediaUri.Category.Track, uri.category)
        Assert.assertEquals("id", uri.id)
        Assert.assertEquals(false, uri.isPodcast)
        Assert.assertEquals(MediaUri.Modifier.MostPlayed, uri.modifier)
        Assert.assertEquals("mediastore:track:id?modifier=mostplayed", uri.toString())
        Assert.assertEquals(listOf("mediastore", "track", "id"), uri.schema)
        Assert.assertEquals("modifier=mostplayed", uri.query)
    }

    @Test
    fun `test podcast with modifier`() {
        val uri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = "id",
            isPodcast = true,
            modifier = MediaUri.Modifier.MostPlayed,
        )

        Assert.assertEquals(MediaUri.Source.MediaStore, uri.source)
        Assert.assertEquals(MediaUri.Category.Track, uri.category)
        Assert.assertEquals("id", uri.id)
        Assert.assertEquals(true, uri.isPodcast)
        Assert.assertEquals(MediaUri.Modifier.MostPlayed, uri.modifier)
        Assert.assertEquals("mediastore:track:id?podcast&modifier=mostplayed", uri.toString())
        Assert.assertEquals(listOf("mediastore", "track", "id"), uri.schema)
        Assert.assertEquals("podcast&modifier=mostplayed", uri.query)
    }

    @Test
    fun `test serialization`() {
        val mediaUri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = "id",
            isPodcast = true,
            modifier = MediaUri.Modifier.MostPlayed,
        )
        val uri = MediaUri(mediaUri.toString())

        Assert.assertEquals(MediaUri.Source.MediaStore, uri.source)
        Assert.assertEquals(MediaUri.Category.Track, uri.category)
        Assert.assertEquals("id", uri.id)
        Assert.assertEquals(true, uri.isPodcast)
        Assert.assertEquals(MediaUri.Modifier.MostPlayed, uri.modifier)
        Assert.assertEquals("mediastore:track:id?podcast&modifier=mostplayed", uri.toString())
        Assert.assertEquals(listOf("mediastore", "track", "id"), uri.schema)
        Assert.assertEquals("podcast&modifier=mostplayed", uri.query)
    }

}