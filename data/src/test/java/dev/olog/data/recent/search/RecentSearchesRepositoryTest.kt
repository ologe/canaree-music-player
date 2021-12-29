package dev.olog.data.recent.search

class RecentSearchesRepositoryTest {

//    private val queries = mock<RecentSearchesQueries>()
//    private val dateTimeFactory = mock<DateTimeFactory>()
//    private val sut = RecentSearchesRepository(
//        schedulers = TestSchedulers(),
//        queries = queries,
//        dateTimeFactory = dateTimeFactory,
//    )
//
//    @Test
//    fun `test getAll`() = runTest {
//        val query = QueryList(
//            SelectAll(MediaId.songId(1), "title 1"),
//            SelectAll(MediaId.songId(2), "title 2"),
//        )
//        whenever(queries.selectAll()).thenReturn(query)
//
//        val expected = listOf(
//            SearchResult(MediaId.songId(1), "title 1"),
//            SearchResult(MediaId.songId(2), "title 2"),
//        )
//
//        sut.observeAll().test(this) {
//            assertValue(expected)
//        }
//    }
//
//    @Test
//    fun `test insert playable`() = runTest {
//        whenever(dateTimeFactory.currentTimeMillis()).thenReturn(100)
//
//        val mediaId = MediaId.songId(1)
//        sut.insert(mediaId)
//
//        verify(queries).insert(
//            item_id = "1",
//            type = 2,
//            insertion_time = 100,
//            media_id = mediaId,
//        )
//    }
//
//    @Test
//    fun `test insert category`() = runTest {
//        whenever(dateTimeFactory.currentTimeMillis()).thenReturn(100)
//
//        val mediaId = MediaId.createCategoryValue(MediaIdCategory.GENRES, "1")
//        sut.insert(mediaId)
//
//        verify(queries).insert(
//            item_id = "1",
//            type = 5,
//            insertion_time = 100,
//            media_id = mediaId,
//        )
//    }
//
//    @Test
//    fun `test delete playable`() = runTest {
//        sut.delete(MediaId.songId(1))
//
//        verify(queries).delete("1", 2)
//    }
//
//    @Test
//    fun `test delete category`() = runTest {
//        sut.delete(MediaId.createCategoryValue(MediaIdCategory.GENRES, "1"))
//
//        verify(queries).delete("1", 5)
//    }
//
//    @Test
//    fun `test deleteAll`() = runTest {
//        sut.deleteAll()
//        verify(queries).deleteAll()
//    }

}