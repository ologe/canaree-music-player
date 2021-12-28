package dev.olog.data

import dev.olog.core.entity.id.PlayableIdentifier
import dev.olog.data.repository.replace
import dev.olog.testing.IndexedPlayables
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PlayingItemQueriesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.playingItemQueries

    companion object {
        private val song = IndexedPlayables(id = 1, is_podcast = false)
        private val podcastEpisode = IndexedPlayables(id = 2, is_podcast = true)
    }

    @Before
    fun setup() {
        indexedQueries.insert(song)
        indexedQueries.insert(podcastEpisode)
    }

    @Test
    fun `initial values should be null`() {
        val actual = queries.select().executeAsOneOrNull()
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test playing item as song`() {
        queries.replace(id = PlayableIdentifier.MediaStore(1, false))

        val actual = queries.select().executeAsOne()

        Assert.assertEquals(song, actual)
    }

    @Test
    fun `test playing item as podcast episode`() {
        queries.replace(id = PlayableIdentifier.MediaStore(2, false))
        val actual = queries.select().executeAsOne()

        Assert.assertEquals(podcastEpisode, actual)
    }

    @Test
    fun `test playing item as song should be null when blacklisted`() {
        blacklistQueries.insert("yes")
        indexedQueries.insert(IndexedPlayables(3L, directory = "yes", is_podcast = false))
        queries.replace(id = PlayableIdentifier.MediaStore(3, false))

        val actual = queries.select().executeAsOneOrNull()

        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test playing item as podcast episode should be null when blacklisted`() {
        blacklistQueries.insert("yes")
        indexedQueries.insert(IndexedPlayables(3L, directory = "yes", is_podcast = true))
        queries.replace(id = PlayableIdentifier.MediaStore(3, true))
        val actual = queries.select().executeAsOneOrNull()

        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test replace, should be only one item`() {
        queries.replace(PlayableIdentifier.MediaStore(1, false))
        queries.replace(PlayableIdentifier.MediaStore(2, false))

        val actual = queries.select().executeAsOne()

        Assert.assertEquals(podcastEpisode, actual)
    }

}