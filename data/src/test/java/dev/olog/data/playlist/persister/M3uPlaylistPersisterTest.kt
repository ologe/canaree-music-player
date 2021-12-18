package dev.olog.data.playlist.persister

import dev.olog.data.playlist.persister.PlaylistPersisterTestItems.items
import org.junit.Assert
import org.junit.Test
import java.io.File

class M3uPlaylistPersisterTest {

    private val persister = M3uPlaylistPersister()

    @Test
    fun test() {
        val file = File("file")
        persister.write(file, items)
        Assert.assertEquals(items, persister.read(file))
    }

}