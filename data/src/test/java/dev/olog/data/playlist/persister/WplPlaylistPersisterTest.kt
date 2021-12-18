package dev.olog.data.playlist.persister

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class WplPlaylistPersisterTest {

    private val persister = WplPlaylistPersister()

    @Test
    fun test() {
        val file = File("file")
        persister.write(file, PlaylistPersisterTestItems.items)
        Assert.assertEquals(PlaylistPersisterTestItems.items, persister.read(file))
    }

}