package dev.olog.data.local.favorite

import dev.olog.domain.entity.favorite.FavoriteEnum
import dev.olog.domain.entity.favorite.FavoriteStateEntity
import dev.olog.domain.entity.favorite.FavoriteType
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.SongGateway
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
) : FavoriteGateway {

    private val favoriteStatePublisher = MutableStateFlow<FavoriteStateEntity?>(null)

    override fun observeToggleFavorite(): Flow<FavoriteEnum> = favoriteStatePublisher
        .filterNotNull()
        .map { it.enum }

    override suspend fun updateFavoriteState(state: FavoriteStateEntity) {
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

    override suspend fun addSingle(type: FavoriteType, songId: Long) {
        val id = favoriteStatePublisher.value?.songId ?: return

        favoriteDao.addToFavoriteSingle(type, songId)
        if (songId == id) {
            updateFavoriteState(
                FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type)
            )
        }
    }

    override suspend fun addGroup(type: FavoriteType, songListId: List<Long>) {
        val id = favoriteStatePublisher.value?.songId ?: return

        favoriteDao.addToFavorite(type, songListId)
        if (songListId.contains(id)) {
            updateFavoriteState(FavoriteStateEntity(id, FavoriteEnum.FAVORITE, type))
        }
    }

    override suspend fun deleteSingle(type: FavoriteType, songId: Long) {
        val id = favoriteStatePublisher.value?.songId ?: return

        favoriteDao.removeFromFavorite(type, listOf(songId))
        if (songId == id) {
            updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
        }
    }

    override suspend fun deleteGroup(type: FavoriteType, songListId: List<Long>) {
        val id = favoriteStatePublisher.value?.songId ?: return

        favoriteDao.removeFromFavorite(type, songListId)
        if (songListId.contains(id)) {
            updateFavoriteState(
                FavoriteStateEntity(id, FavoriteEnum.NOT_FAVORITE, type)
            )
        }
    }

    override suspend fun deleteAll(type: FavoriteType) {
        val id = favoriteStatePublisher.value?.songId ?: return

        favoriteDao.deleteTracks()
        updateFavoriteState(FavoriteStateEntity(id, FavoriteEnum.NOT_FAVORITE, type))
    }

    override suspend fun isFavorite(type: FavoriteType, songId: Long): Boolean {
        return favoriteDao.isFavorite(songId) != null
    }

    override suspend fun toggleFavorite() {
        val value = favoriteStatePublisher.value ?: return
        val id = value.songId
        val state = value.enum
        val type = value.favoriteType

        when (state) {
            FavoriteEnum.NOT_FAVORITE -> {
                updateFavoriteState(
                    FavoriteStateEntity(id, FavoriteEnum.FAVORITE, type)
                )
                favoriteDao.addToFavoriteSingle(type, id)
            }
            FavoriteEnum.FAVORITE -> {
                updateFavoriteState(
                    FavoriteStateEntity(id, FavoriteEnum.NOT_FAVORITE, type)
                )
                favoriteDao.removeFromFavorite(type, listOf(id))
            }
        }
    }
}