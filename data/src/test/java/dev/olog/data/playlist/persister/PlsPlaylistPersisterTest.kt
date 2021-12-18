package dev.olog.data.playlist.persister

import org.junit.Assert
import org.junit.Test
import java.io.File

class PlsPlaylistPersisterTest {

    private val persister = PlsPlaylistPersister()

    @Test
    fun test() {
        val file = File("file")
        persister.write(file, PlaylistPersisterTestItems.items)
        Assert.assertEquals(PlaylistPersisterTestItems.items, persister.read(file))
    }

}