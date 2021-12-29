package dev.olog.data

import dev.olog.data.playable.Podcast_episodes_view
import dev.olog.data.playable.Songs_view
import dev.olog.data.repository.replace
import dev.olog.testing.IndexedTrack
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FavoritesQueriesTest {

    private val db = TestDatabase()
    private val blacklistQueries = db.blacklistQueries
    private val indexedQueries = db.indexedPlayablesQueries
    private val queries = db.favoritesQueries

    companion object {
        private val songs = listOf(
            IndexedTrack(id = "1", is_podcast = false),
            IndexedTrack(id = "2", is_podcast = false),
            IndexedTrack(id = "3", is_podcast = false),
        )

        private val podcasts = listOf(
            IndexedTrack(id = "4", is_podcast = true),
            IndexedTrack(id = "5", is_podcast = true),
            IndexedTrack(id = "6", is_podcast = true),
        )
    }

    @Before
    fun setup() {
        // blacklist
        blacklistQueries.insert("yes")
        indexedQueries.insert(IndexedTrack(id = "1000", is_podcast = false))
        indexedQueries.insert(IndexedTrack(id = "1001", is_podcast = true))

        for (playable in songs + podcasts) {
            indexedQueries.insert(playable)
        }
    }

    @Test
    fun `test select AllSongs favorites`() {
        val expected = songs
        expected.forEach { queries.insert(it.id) }

        val actual = queries.selectAllSongs().executeAsList()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test select AllPodcasts favorites`() {
        val expected = podcasts
        expected.forEach { queries.insert(it.id) }

        val actual = queries.selectAllPodcastEpisodes().executeAsList()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test playing item favorite`() {
        val playingItemQueries = db.playingItemQueries
        Assert.assertEquals(null, queries.selectCurrentFavorite().executeAsOneOrNull())

        // test song
        playingItemQueries.replace("1")
        queries.addPlayingItemToFavorites()

        val actualSong = queries.selectCurrentFavorite().executeAsOne()
        val expectedSong = Favorites("1", false)

        Assert.assertEquals(expectedSong, actualSong)

        // test podcast
        playingItemQueries.replace("4")
        queries.addPlayingItemToFavorites()

        val actualPodcast = queries.selectCurrentFavorite().executeAsOne()
        val expectedPodcast = Favorites("4", true)

        Assert.assertEquals(expectedPodcast, actualPodcast)

        // remove
        queries.removePlayingItemFromFavorites()

        Assert.assertEquals(null, queries.selectCurrentFavorite().executeAsOneOrNull())
    }

    @Test
    fun `test clear songs`() {
        // empty initially
        Assert.assertEquals(emptyList<Songs_view>(), queries.selectAllSongs().executeAsList())
        Assert.assertEquals(emptyList<Podcast_episodes_view>(), queries.selectAllPodcastEpisodes().executeAsList())

        val song = songs.first()
        val podcast = podcasts.first()

        // insert some data
        queries.insert(song.id)
        queries.insert(podcast.id)

        Assert.assertEquals(listOf(song), queries.selectAllSongs().executeAsList())
        Assert.assertEquals(listOf(podcast), queries.selectAllPodcastEpisodes().executeAsList())

        // delete songs
        queries.clear(is_podcast = false)
        Assert.assertEquals(emptyList<Songs_view>(), queries.selectAllSongs().executeAsList())
        Assert.assertEquals(listOf(podcast), queries.selectAllPodcastEpisodes().executeAsList())

        // delete podcasts
        queries.clear(is_podcast = true)
        Assert.assertEquals(emptyList<Songs_view>(), queries.selectAllSongs().executeAsList())
        Assert.assertEquals(emptyList<Podcast_episodes_view>(), queries.selectAllPodcastEpisodes().executeAsList())
    }

    @Test
    fun `test delete`() {
        // empty initially
        Assert.assertEquals(emptyList<Songs_view>(), queries.selectAllSongs().executeAsList())
        Assert.assertEquals(emptyList<Podcast_episodes_view>(), queries.selectAllPodcastEpisodes().executeAsList())

        val song = songs.first()
        val podcast = podcasts.first()

        // insert some data
        queries.insert(song.id)
        queries.insert(podcast.id)

        Assert.assertEquals(listOf(song), queries.selectAllSongs().executeAsList())
        Assert.assertEquals(listOf(podcast), queries.selectAllPodcastEpisodes().executeAsList())

        // delete song
        queries.delete(song.id)
        Assert.assertEquals(emptyList<Songs_view>(), queries.selectAllSongs().executeAsList())
        Assert.assertEquals(listOf(podcast), queries.selectAllPodcastEpisodes().executeAsList())

        // delete podcast
        queries.delete(podcast.id)
        Assert.assertEquals(emptyList<Songs_view>(), queries.selectAllSongs().executeAsList())
        Assert.assertEquals(emptyList<Podcast_episodes_view>(), queries.selectAllPodcastEpisodes().executeAsList())
    }

    @Test
    fun `test isFavorite`() {
        Assert.assertEquals(null, queries.isFavorite("1").executeAsOneOrNull())

        queries.insert("1")

        val actual = queries.isFavorite("1").executeAsOne()
        val expected = Favorites("1", false)

        Assert.assertEquals(expected, actual)
    }

}