package dev.olog.data.repository

import com.nhaarman.mockitokotlin2.*
import dev.olog.core.entity.favorite.FavoriteState
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.data.Mocks
import dev.olog.data.db.FavoriteDao
import dev.olog.data.model.db.FavoritePodcastEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

internal class FavoriteRepositoryTest {

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private val dao = mock<FavoriteDao>()
    private val songGateway = mock<TrackGateway>()
    private val podcastGateway = mock<PodcastGateway>()
    private val sut = FavoriteRepository(dao, songGateway, podcastGateway)

    @Test
    fun shouldInitialStateBeNull() {
        Assert.assertNull(sut.getState())
    }

    @Test
    fun `should update state`() = coroutinesRule.runBlockingTest {
        // given
        val state = dev.olog.core.entity.favorite.FavoriteEntity(1, FavoriteState.FAVORITE, FavoriteTrackType.TRACK)
        sut.getState()

        // when
        sut.updateFavoriteState(state)

        // then
        assertEquals(
            state,
            sut.getState()
        )
    }

    @Test
    fun `should return only favorite tracks present in library`() {
        // given n tracks
        val tracks = listOf(
            Mocks.song.copy(id = 1),
            Mocks.song.copy(id = 2)
        )

        val favorites = listOf(
            1L, // exists
            200L // not exists
        )

        whenever(dao.getAllTracksImpl()).thenReturn(favorites)
        whenever(songGateway.getAll()).thenReturn(tracks)
        verifyNoMoreInteractions(dao)
        verifyNoMoreInteractions(songGateway)

        // when
        val actual = sut.getTracks()

        // then
        verify(dao).getAllTracksImpl()
        verify(songGateway).getAll()

        assertEquals(
            listOf(Mocks.song.copy(id = 1)),
            actual
        )
    }

    @Test
    fun `should return only favorite podcasts present in library`() {
        // given n tracks
        val podcasts = listOf(
            Mocks.podcast.copy(id = 1),
            Mocks.podcast.copy(id = 2)
        )

        val favorites = listOf(
            1L, // exists
            200L // not exists
        )

        whenever(dao.getAllPodcastsImpl()).thenReturn(favorites)
        whenever(podcastGateway.getAll()).thenReturn(podcasts)

        // when
        val actual = sut.getPodcasts()

        // then
        verify(dao).getAllPodcastsImpl()
        verify(podcastGateway).getAll()
        verifyNoMoreInteractions(dao)
        verifyNoMoreInteractions(songGateway)

        assertEquals(
            listOf(Mocks.podcast.copy(id = 1)),
            actual
        )
    }

    @Test
    fun `should observe tracks`() = coroutinesRule.runBlockingTest {
        // given n tracks
        val tracks = listOf(
            Mocks.song.copy(id = 1),
            Mocks.song.copy(id = 2)
        )

        val favorites = listOf(
            1L, // exists
            200L // not exists
        )

        whenever(dao.observeAllTracksImpl()).thenReturn(flowOf(favorites))
        whenever(songGateway.getAll()).thenReturn(tracks)

        // when
        val actual = sut.observeTracks().first()

        verify(dao).observeAllTracksImpl()
        verify(songGateway).getAll()
        verifyNoMoreInteractions(dao)
        verifyNoMoreInteractions(songGateway)

        assertEquals(
            listOf(Mocks.song.copy(id = 1)),
            actual
        )
    }

    @Test
    fun `should observe podcasts`() = coroutinesRule.runBlockingTest {
        // given n tracks
        val tracks = listOf(
            Mocks.podcast.copy(id = 1),
            Mocks.podcast.copy(id = 2)
        )

        val favorites = listOf(
            1L, // exists
            200L // not exists
        )

        whenever(dao.observeAllPodcastsImpl()).thenReturn(flowOf(favorites))
        whenever(podcastGateway.getAll()).thenReturn(tracks)
        verifyNoMoreInteractions(dao)
        verifyNoMoreInteractions(songGateway)

        // when
        val actual = sut.observePodcasts().first()

        verify(dao).observeAllPodcastsImpl()
        verify(podcastGateway).getAll()
        verifyNoMoreInteractions(dao)
        verifyNoMoreInteractions(podcastGateway)

        assertEquals(
            listOf(Mocks.podcast.copy(id = 1)),
            actual
        )
    }

    @Test
    fun `should add single track`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L

        // when
        sut.addSingle(FavoriteTrackType.TRACK, id)

        // then
        verify(dao).insertOneImpl(FavoriteEntity(id))
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should add single track and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(
                id,
                FavoriteState.NOT_FAVORITE,
                FavoriteTrackType.TRACK
            )
        )

        // when
        sut.addSingle(FavoriteTrackType.TRACK, id)

        // then
        verify(dao).insertOneImpl(FavoriteEntity(id))
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.TRACK),
            sut.getState()
        )
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should add single podcast`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L

        // when
        sut.addSingle(FavoriteTrackType.PODCAST, id)

        // then
        verify(dao).insertOnePodcastImpl(
            FavoritePodcastEntity(
                id
            )
        )
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should add single podcast and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(
                id,
                FavoriteState.NOT_FAVORITE,
                FavoriteTrackType.PODCAST
            )
        )

        // when
        sut.addSingle(FavoriteTrackType.PODCAST, id)

        // then
        verify(dao).insertOnePodcastImpl(
            FavoritePodcastEntity(
                id
            )
        )
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.PODCAST),
            sut.getState()
        )
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should add group track`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val list = listOf(id)

        // when
        sut.addGroup(FavoriteTrackType.TRACK, list)

        // then
        verify(dao).insertGroupImpl(list.map {
            FavoriteEntity(
                it
            )
        })
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should add group track and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val list = listOf(1L)
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(
                id,
                FavoriteState.NOT_FAVORITE,
                FavoriteTrackType.TRACK
            )
        )

        // when
        sut.addGroup(FavoriteTrackType.TRACK, list)

        // then
        verify(dao).insertGroupImpl(list.map {
            FavoriteEntity(
                it
            )
        })
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.TRACK),
            sut.getState()
        )
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should add group podcast`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val list = listOf(id)

        // when
        sut.addGroup(FavoriteTrackType.PODCAST, list)

        // then
        verify(dao).insertGroupPodcastImpl(list.map {
            FavoritePodcastEntity(
                it
            )
        })
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should add group podcast and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val list = listOf(id)
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(
                id,
                FavoriteState.NOT_FAVORITE,
                FavoriteTrackType.PODCAST
            )
        )

        // when
        sut.addGroup(FavoriteTrackType.PODCAST, list)

        // then
        verify(dao).insertGroupPodcastImpl(list.map {
            FavoritePodcastEntity(
                it
            )
        })
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.PODCAST),
            sut.getState()
        )
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should delete single track`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L

        // when
        sut.deleteSingle(FavoriteTrackType.TRACK, id)

        // then
        verify(dao).deleteGroupImpl(listOf(
            FavoriteEntity(
                id
            )
        ))
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should delete single track and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.updateFavoriteState(dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.TRACK))

        // when
        sut.deleteSingle(FavoriteTrackType.TRACK, id)

        // then
        verify(dao).deleteGroupImpl(listOf(
            FavoriteEntity(
                id
            )
        ))
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.NOT_FAVORITE, FavoriteTrackType.TRACK),
            sut.getState()
        )
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should delete single podcast`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L

        // when
        sut.deleteSingle(FavoriteTrackType.PODCAST, id)

        // then
        verify(dao).deleteGroupPodcastImpl((listOf(
            FavoritePodcastEntity(
                id
            )
        )))
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should delete single podcast and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(
                id,
                FavoriteState.FAVORITE,
                FavoriteTrackType.PODCAST
            )
        )

        // when
        sut.deleteSingle(FavoriteTrackType.PODCAST, id)

        // then
        verify(dao).deleteGroupPodcastImpl(listOf(
            FavoritePodcastEntity(
                id
            )
        ))
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.NOT_FAVORITE, FavoriteTrackType.PODCAST),
            sut.getState()
        )
        verifyNoMoreInteractions(dao)
    }


    @Test
    fun `should delete group track`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val list = listOf(id)

        // when
        sut.deleteGroup(FavoriteTrackType.TRACK, list)

        // then
        verify(dao).deleteGroupImpl(list.map {
            FavoriteEntity(
                it
            )
        })
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should delete group track and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val list = listOf(1L)
        sut.updateFavoriteState(dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.TRACK))

        // when
        sut.deleteGroup(FavoriteTrackType.TRACK, list)

        // then
        verify(dao).deleteGroupImpl(list.map {
            FavoriteEntity(
                it
            )
        })
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.NOT_FAVORITE, FavoriteTrackType.TRACK),
            sut.getState()
        )
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should delete group podcast`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val list = listOf(id)

        // when
        sut.deleteGroup(FavoriteTrackType.PODCAST, list)

        // then
        verify(dao).deleteGroupPodcastImpl(list.map {
            FavoritePodcastEntity(
                it
            )
        })
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should delete group podcast and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val list = listOf(id)
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.PODCAST)
        )

        // when
        sut.deleteGroup(FavoriteTrackType.PODCAST, list)

        // then
        verify(dao).deleteGroupPodcastImpl(list.map {
            FavoritePodcastEntity(
                it
            )
        })
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.NOT_FAVORITE, FavoriteTrackType.PODCAST),
            sut.getState()
        )
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `should delete all tracks`() = coroutinesRule.runBlockingTest {
        // when
        sut.deleteAll(FavoriteTrackType.TRACK)

        // then
        verify(dao).deleteAllTracks()
    }

    @Test
    fun `should delete all tracks and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.updateFavoriteState(dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.TRACK))

        // when
        sut.deleteAll(FavoriteTrackType.TRACK)

        // then
        verify(dao).deleteAllTracks()
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.NOT_FAVORITE, FavoriteTrackType.TRACK),
            sut.getState()
        )
    }

    @Test
    fun `should delete all podcasts`() = coroutinesRule.runBlockingTest {
        // when
        sut.deleteAll(FavoriteTrackType.PODCAST)

        // then
        verify(dao).deleteAllPodcasts()
    }

    @Test
    fun `should delete all podcasts and update favorite state`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.PODCAST)
        )

        // when
        sut.deleteAll(FavoriteTrackType.PODCAST)

        // then
        verify(dao).deleteAllPodcasts()
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.NOT_FAVORITE, FavoriteTrackType.PODCAST),
            sut.getState()
        )
    }

    @Test
    fun `test favorite track`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        whenever(dao.isFavorite(id)).thenReturn(true)

        // then
        sut.isFavorite(id, FavoriteTrackType.TRACK)

        // then
        verify(dao).isFavorite(id)
    }

    @Test
    fun `test favorite podcast`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        whenever(dao.isFavoritePodcast(id)).thenReturn(true)

        // when
        sut.isFavorite(id, FavoriteTrackType.PODCAST)

        // then
        verify(dao).isFavoritePodcast(id)
    }

    @Test
    fun `test toggle but not old state found`() = coroutinesRule.runBlockingTest {
        sut.toggleFavorite()
        verifyZeroInteractions(dao)
        verifyZeroInteractions(songGateway)
        verifyZeroInteractions(podcastGateway)
    }

    @Test
    fun `test toggle from not favorite to favorite`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.NOT_FAVORITE, FavoriteTrackType.TRACK)
        )

        // when
        sut.toggleFavorite()

        // then
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.TRACK),
            sut.getState()
        )
        verify(dao).insertOneImpl(FavoriteEntity(id))
    }

    @Test
    fun `test toggle from favorite to not favorite`() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.FAVORITE, FavoriteTrackType.TRACK)
        )

        // when
        sut.toggleFavorite()

        // then
        assertEquals(
            dev.olog.core.entity.favorite.FavoriteEntity(id, FavoriteState.NOT_FAVORITE, FavoriteTrackType.TRACK),
            sut.getState()
        )
        verify(dao).deleteGroupImpl(listOf(
            FavoriteEntity(
                id
            )
        ))
    }

}