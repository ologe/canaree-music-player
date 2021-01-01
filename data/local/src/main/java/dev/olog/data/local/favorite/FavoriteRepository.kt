package dev.olog.data.local.favorite

import dev.olog.domain.entity.Favorite
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.SongGateway
import dev.olog.shared.exhaustive
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
) : FavoriteGateway {

    private val favoriteStatePublisher = MutableStateFlow<Favorite?>(null)

    override fun observeToggleFavorite(): Flow<Favorite.State> = favoriteStatePublisher
        .filterNotNull()
        .map { it.state }

    override suspend fun updateFavoriteState(state: Favorite) {
        favoriteStatePublisher.value = state
    }

    override suspend fun getTracks(): List<Track> {
        val favoriteList = favoriteDao.getAllTracksImpl()
        val songList: Map<Long, List<Track>> = songGateway.getAll().groupBy { it.id }
        return favoriteList
            .mapNotNull { id -> songList[id]?.get(0) }
            .sortedBy { it.title }
    }

    override suspend fun getPodcasts(): List<Track> {
        val favoriteList = favoriteDao.getAllPodcastsImpl()
        val songList: Map<Long, List<Track>> = songGateway.getAll().groupBy { it.id }
        return favoriteList
            .mapNotNull { id -> songList[id]?.get(0) }
            .sortedBy { it.title }
    }

    override fun observeTracks(): Flow<List<Track>> {
        return favoriteDao.observeAllTracksImpl()
            .map { favoriteList ->
                val songs: Map<Long, List<Track>> = songGateway.getAll().groupBy { it.id }
                favoriteList.mapNotNull { id -> songs[id]?.get(0) }
                    .sortedBy { it.title }
            }
    }

    override fun observePodcasts(): Flow<List<Track>> {
        return favoriteDao.observeAllPodcastsImpl()
            .map { favoriteList ->
                val podcast: Map<Long, List<Track>> = podcastGateway.getAll().groupBy { it.id }
                favoriteList.mapNotNull { id -> podcast[id]?.get(0) }
                    .sortedBy { it.title }
            }
    }

    override suspend fun addSingle(type: Favorite.Type, trackId: Long) {
        val id = favoriteStatePublisher.value?.trackId ?: return

        favoriteDao.addToFavoriteSingle(type, trackId)
        if (trackId == id) {
            updateFavoriteState(
                state = Favorite(
                    trackId = trackId,
                    state = Favorite.State.FAVORITE,
                    favoriteType = type
                )
            )
        }
    }

    override suspend fun addGroup(type: Favorite.Type, trackList: List<Long>) {
        val id = favoriteStatePublisher.value?.trackId ?: return

        favoriteDao.addToFavorite(type, trackList)
        if (trackList.contains(id)) {
            updateFavoriteState(state = Favorite(
                trackId = id,
                state = Favorite.State.FAVORITE,
                favoriteType = type
            )
            )
        }
    }

    override suspend fun deleteSingle(type: Favorite.Type, trackId: Long) {
        val id = favoriteStatePublisher.value?.trackId ?: return

        favoriteDao.removeFromFavorite(type, listOf(trackId))
        if (trackId == id) {
            updateFavoriteState(state = Favorite(
                trackId = trackId,
                state = Favorite.State.NOT_FAVORITE,
                favoriteType = type
            )
            )
        }
    }

    override suspend fun deleteGroup(type: Favorite.Type, trackList: List<Long>) {
        val id = favoriteStatePublisher.value?.trackId ?: return

        favoriteDao.removeFromFavorite(type, trackList)
        if (trackList.contains(id)) {
            updateFavoriteState(
                state = Favorite(
                    trackId = id,
                    state = Favorite.State.NOT_FAVORITE,
                    favoriteType = type
                )
            )
        }
    }

    override suspend fun deleteAll(type: Favorite.Type) {
        val id = favoriteStatePublisher.value?.trackId ?: return

        favoriteDao.deleteTracks()
        updateFavoriteState(state = Favorite(
            trackId = id,
            state = Favorite.State.NOT_FAVORITE,
            favoriteType = type
        )
        )
    }

    override suspend fun isFavorite(type: Favorite.Type, trackId: Long): Boolean {
        return favoriteDao.isFavorite(trackId) != null
    }

    override suspend fun toggleFavorite() {
        val value = favoriteStatePublisher.value ?: return
        val id = value.trackId
        val state = value.state
        val type = value.favoriteType

        when (state) {
            Favorite.State.NOT_FAVORITE -> {
                updateFavoriteState(
                    Favorite(id, Favorite.State.FAVORITE, type)
                )
                favoriteDao.addToFavoriteSingle(type, id)
            }
            Favorite.State.FAVORITE -> {
                updateFavoriteState(
                    Favorite(id, Favorite.State.NOT_FAVORITE, type)
                )
                favoriteDao.removeFromFavorite(type, listOf(id))
            }
        }.exhaustive
    }
}