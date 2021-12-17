//package dev.olog.data.index
//
//import android.content.ContentResolver
//import android.content.Context
//import com.nhaarman.mockitokotlin2.doReturn
//import com.nhaarman.mockitokotlin2.mock
//import com.nhaarman.mockitokotlin2.whenever
//import dev.olog.shared.android.permission.PermissionManager
//import dev.olog.test.shared.TestSchedulers
//import dev.olog.testing.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.test.runTest
//import org.junit.Before
//import org.junit.Test
//
//class IndexingRepositoryTest {
//
//    private val contentResolver = mock<ContentResolver>()
//    private val context = mock<Context> {
//        on { contentResolver } doReturn contentResolver
//    }
//    private val permissionManager = mock<PermissionManager>()
//    private val indexedPlayablesQueries = mock<IndexedPlayablesQueries>()
//    private val indexingGenresQueries = mock<IndexedGenresQueries>()
//    private val indexingPlaylistsQueries = mock<IndexedPlaylistsQueries>()
//    private val mediaStoreManager = mock<MediaStoreManager>()
//    private val repo = IndexingRepository(
//        context = context,
//        schedulers = TestSchedulers(),
//        appScope = CoroutineScope(Dispatchers.Unconfined),
//        permissionManager = permissionManager,
//        indexedPlayablesQueries = indexedPlayablesQueries,
//        indexingGenresQueries = indexingGenresQueries,
//        indexingPlaylistsQueries = indexingPlaylistsQueries,
//        mediaStoreManager = mediaStoreManager
//    )
//
//    @Before
//    fun setup() {
//        whenever(mediaStoreManager.playables()).thenReturn(
//            listOf(emptyIndexedPlayables().copy(id = 1L))
//        )
//
//        whenever(mediaStoreManager.genres()).thenReturn(
//            listOf(
//                emptyIndexedGenre().copy(id = 1L),
//                emptyIndexedGenre().copy(id = 2L),
//            )
//        )
//
//        whenever(mediaStoreManager.genreItems(1)).thenReturn(
//            listOf(
//                emptyIndexedGenrePlayables().copy(genre_id = 1L, song_id = 10L),
//                emptyIndexedGenrePlayables().copy(genre_id = 1L, song_id = 20L),
//                emptyIndexedGenrePlayables().copy(genre_id = 2L, song_id = 30L),
//            )
//        )
//
//        whenever(mediaStoreManager.playlists()).thenReturn(
//            listOf(
//                emptyIndexedPlaylist().copy(id = 10L),
//                emptyIndexedPlaylist().copy(id = 20L),
//            )
//        )
//
//        whenever(mediaStoreManager.playlistsItems(1)).thenReturn(
//            listOf(
//                emptyIndexedPlaylistPlayables().copy(playlist_id = 10L, playable_id = 10L),
//                emptyIndexedPlaylistPlayables().copy(playlist_id = 10L, playable_id = 20L),
//                emptyIndexedPlaylistPlayables().copy(playlist_id = 20L, playable_id = 30L),
//            )
//        )
//    }
//
//    @Test
//    fun `test init`() = runTest {
//        repo.init()
//    }
//
//}