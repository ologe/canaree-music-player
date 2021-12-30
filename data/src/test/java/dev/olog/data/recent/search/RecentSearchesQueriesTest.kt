package dev.olog.data.recent.search

class RecentSearchesQueriesTest {

//    private val db = TestDatabase() todo
//    private val indexedPlayableQueries = db.indexedPlayablesQueries
//    private val indexedGenreQueries = db.indexedGenresQueries
//    private val indexedPlaylistQueries = db.indexedPlaylistsQueries
//    private val queries = db.recentSearchesQueries
//
//    companion object {
//        const val FOLDER_ID = "directory"
//        const val SONG_ID = 1L
//        const val ARTIST_ID = 2L
//        const val ALBUM_ID = 3L
//        const val GENRE_ID = 4L
//        const val PLAYLIST_ID = 5L
//
//        const val PODCAST_ID = 10L
//        const val PODCAST_AUTHOR_ID = 11L
//        const val PODCAST_COLLECTION_ID = 12L
//        const val PODCAST_PLAYLIST_ID = 13L
//    }
//
//    @Test
//    fun test() {
//        indexedPlayableQueries.insert(
//            IndexedPlayables(
//                id = SONG_ID,
//                author_id = ARTIST_ID,
//                collection_id = ALBUM_ID,
//                title = "title",
//                author = "author",
//                collection = "collection",
//                directory = FOLDER_ID,
//                is_podcast = false
//            )
//        )
//        indexedPlayableQueries.insert(
//            IndexedPlayables(
//                id = PODCAST_ID,
//                author_id = PODCAST_AUTHOR_ID,
//                collection_id = PODCAST_COLLECTION_ID,
//                title = "podcast title",
//                author = "podcast author",
//                collection = "podcast collection",
//                directory = "",
//                is_podcast = true
//            )
//        )
//        indexedGenreQueries.insert(Indexed_genres(GENRE_ID, "genre"))
//        indexedGenreQueries.insertPlayable(Indexed_genres_playables(GENRE_ID, SONG_ID))
//
//        indexedPlaylistQueries.insert(Indexed_playlists(PLAYLIST_ID, "playlist", ""))
//        indexedPlaylistQueries.insert(Indexed_playlists(PODCAST_PLAYLIST_ID, "podcast playlist", ""))
//        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(1, PLAYLIST_ID, SONG_ID, 0))
//        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(2, PODCAST_PLAYLIST_ID, PODCAST_ID, 0))
//
//        queries.insert(
//            mediaId = createCategoryValue(FOLDERS, FOLDER_ID),
//            insertion_time = 10
//        )
//        queries.insert(
//            mediaId = createCategoryValue(PLAYLISTS, "$PLAYLIST_ID"),
//            insertion_time = 1
//        )
//        queries.insert(
//            mediaId = songId(SONG_ID),
//            insertion_time = 2
//        )
//        queries.insert(
//            mediaId = createCategoryValue(ARTISTS, "$ARTIST_ID"),
//            insertion_time = 3
//        )
//        queries.insert(
//            mediaId = createCategoryValue(ALBUMS, "$ALBUM_ID"),
//            insertion_time = 4
//        )
//        queries.insert(
//            mediaId = createCategoryValue(GENRES, "$GENRE_ID"),
//            insertion_time = 5
//        )
//
//        queries.insert(
//            mediaId = createCategoryValue(PODCASTS_PLAYLIST, "$PODCAST_PLAYLIST_ID"),
//            insertion_time = 6
//        )
//        queries.insert(
//            mediaId = podcastId(PODCAST_ID),
//            insertion_time = 7
//        )
//        queries.insert(
//            mediaId = createCategoryValue(PODCASTS_ARTISTS, "$PODCAST_AUTHOR_ID"),
//            insertion_time = 8
//        )
//        queries.insert(
//            mediaId = createCategoryValue(PODCASTS_ALBUMS, "$PODCAST_COLLECTION_ID"),
//            insertion_time = 9
//        )
//
//        val actual = queries.selectAll().executeAsList()
//        val expected = listOf(
//            SelectAll(createCategoryValue(FOLDERS, FOLDER_ID), "directory"),
//            SelectAll(createCategoryValue(PODCASTS_ALBUMS, "$PODCAST_COLLECTION_ID"), "podcast collection"),
//            SelectAll(createCategoryValue(PODCASTS_ARTISTS, "$PODCAST_AUTHOR_ID"), "podcast author"),
//            SelectAll(podcastId(PODCAST_ID), "podcast title"),
//            SelectAll(createCategoryValue(PODCASTS_PLAYLIST, "$PODCAST_PLAYLIST_ID"), "podcast playlist"),
//            SelectAll(createCategoryValue(GENRES, "$GENRE_ID"), "genre"),
//            SelectAll(createCategoryValue(ALBUMS, "$ALBUM_ID"), "collection"),
//            SelectAll(createCategoryValue(ARTISTS, "$ARTIST_ID"), "author"),
//            SelectAll(songId(SONG_ID), "title"),
//            SelectAll(createCategoryValue(PLAYLISTS, "$PLAYLIST_ID"), "playlist"),
//        )
//
//        Assert.assertEquals(expected, actual)
//    }
//
//    @Test
//    fun `test insert with same id, but different type, then delete`() {
//        indexedPlayableQueries.insert(
//            IndexedPlayables(
//                id = 1L,
//                author_id = 1L,
//                collection_id = 1L,
//                title = "title",
//                author = "author",
//                collection = "collection",
//                is_podcast = false
//            )
//        )
//
//        // insert
//        queries.insert(
//            mediaId = songId(1),
//            insertion_time = 1
//        )
//        queries.insert(
//            mediaId = createCategoryValue(ARTISTS, "1"),
//            insertion_time = 2
//        )
//        queries.insert(
//            mediaId = createCategoryValue(ALBUMS, "1"),
//            insertion_time = 3
//        )
//
//        val actualInsert = queries.selectAll().executeAsList()
//        val expectedInsert = listOf(
//            SelectAll(createCategoryValue(ALBUMS, "1"), "collection"),
//            SelectAll(createCategoryValue(ARTISTS, "1"), "author"),
//            SelectAll(songId(1), "title"),
//        )
//        Assert.assertEquals(expectedInsert, actualInsert)
//
//        // delete single
//        queries.delete("1", songId(1).category.recentSearchType())
//
//        val actualDeleteSingle = queries.selectAll().executeAsList()
//        val expectedDeleteSingle = listOf(
//            SelectAll(createCategoryValue(ALBUMS, "1"), "collection"),
//            SelectAll(createCategoryValue(ARTISTS, "1"), "author"),
//        )
//        Assert.assertEquals(expectedDeleteSingle, actualDeleteSingle)
//
//        // delete all
//        queries.deleteAll()
//
//        val actualDeleteAll = queries.selectAll().executeAsList()
//        Assert.assertEquals(emptyList<SelectAll>(), actualDeleteAll)
//    }


}