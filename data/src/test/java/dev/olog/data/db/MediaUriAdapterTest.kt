package dev.olog.data.db

import dev.olog.core.MediaUri
import org.junit.Assert
import org.junit.Test

class MediaUriAdapterTest {

    @Test
    fun test() {
        val adapter = MediaUriAdapter

        val expected = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = "id",
            isPodcast = true,
            modifier = MediaUri.Modifier.MostPlayed
        )

        val actual = adapter.decode(adapter.encode(expected))
        Assert.assertEquals(expected, actual)
    }

}