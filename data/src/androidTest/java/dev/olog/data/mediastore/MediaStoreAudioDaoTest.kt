package dev.olog.data.mediastore

import dev.olog.data.DatabaseTest
import dev.olog.data.emptyMediaStoreAudioEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MediaStoreAudioDaoTest : DatabaseTest() {

    private val dao = db.mediaStoreAudioDao()

    @Test
    fun test() = runTest {
        // initial state, empty
        Assert.assertEquals(emptyList<List<MediaStoreAudioEntity>>(), dao.getAll())

        // insert one item
        dao.insertAll(listOf(emptyMediaStoreAudioEntity(id = "1", isPodcast = false)))

        Assert.assertEquals(listOf(emptyMediaStoreAudioEntity(id = "1", isPodcast = false)), dao.getAll())

        // insert multiple items, also with conflicts
        dao.insertAll(listOf(emptyMediaStoreAudioEntity(id = "1", isPodcast = false)))
        dao.insertAll(listOf(
            emptyMediaStoreAudioEntity(id = "1", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "2", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "3", isPodcast = true),
        ))

        Assert.assertEquals(
            listOf(
                emptyMediaStoreAudioEntity(id = "1", isPodcast = true),
                emptyMediaStoreAudioEntity(id = "2", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "3", isPodcast = true),
            ),
            dao.getAll()
        )

        // replace items
        dao.replaceAll(
            listOf(
                emptyMediaStoreAudioEntity(id = "4", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "5", isPodcast = true),
            )
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreAudioEntity(id = "4", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "5", isPodcast = true),
            ),
            dao.getAll()
        )

        // clear
        dao.deleteAll()

        Assert.assertEquals(emptyList<List<MediaStoreAudioEntity>>(), dao.getAll())
    }

}