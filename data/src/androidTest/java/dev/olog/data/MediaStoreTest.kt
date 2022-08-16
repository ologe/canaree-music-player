package dev.olog.data

import dev.olog.data.blacklist.db.BlacklistEntity
import kotlinx.coroutines.test.runTest
import org.junit.Before

abstract class MediaStoreTest(
    private val isPodcastTest: Boolean,
) : DatabaseTest() {

    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val blacklistDao = db.blacklistDao()

    @Before
    fun setup() = runTest {
        val blacklisted = "blacklisted"
        mediaStoreDao.insertAll(
            listOf(
                // blacklisted song
                emptyMediaStoreAudioEntity(
                    id = "-1",
                    artistId = "-10",
                    albumId = "-100",
                    directory = blacklisted,
                    isPodcast = isPodcastTest
                ),
                // blacklisted podcast
                emptyMediaStoreAudioEntity(
                    id = "-2",
                    artistId = "-20",
                    albumId = "-200",
                    directory = blacklisted,
                    isPodcast = isPodcastTest
                ),
                // non blacklisted podcast
                emptyMediaStoreAudioEntity(
                    id = "-3",
                    artistId = "-30",
                    albumId = "-300",
                    directory = "non blacklisted",
                    isPodcast = !isPodcastTest
                ),
            )
        )
        blacklistDao.insertAll(listOf(BlacklistEntity(blacklisted)))
    }

}