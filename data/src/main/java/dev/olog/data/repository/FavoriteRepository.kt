package dev.olog.data.repository

import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.data.db.dao.FavoriteDao
import dev.olog.data.mediastore.audio.toSong
import dev.olog.shared.assertBackgroundThread
import dev.olog.shared.mapListItem
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
) : FavoriteGateway {

    private val favoriteStatePublisher = ConflatedBroadcastChannel<FavoriteStateEntity>()

    override fun observeToggleFavorite(): Flow<FavoriteEnum> = favoriteStatePublisher
        .asFlow()
        .map { it.enum }

    override suspend fun updateFavoriteState(state: FavoriteStateEntity) {
        favoriteStatePublisher.trySend(state)
    }

    override fun getTracks(): List<Song> {
        return favoriteDao.getAllTracks().map { it.toSong() }
    }

    override fun getPodcasts(): List<Song> {
        return favoriteDao.getAllPodcasts().map { it.toSong() }
    }

    override fun observeTracks(): Flow<List<Song>> {
        return favoriteDao.observeAllTracks().mapListItem { it.toSong() }
    }

    override fun observePodcasts(): Flow<List<Song>> {
        return favoriteDao.observeAllPodcasts().mapListItem { it.toSong() }
    }

    override suspend fun addSingle(type: FavoriteType, songId: Long) {
        favoriteDao.addToFavoriteSingle(type, songId)
        val id = favoriteStatePublisher.value.songId
        if (songId == id) {
            updateFavoriteState(
                FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type)
            )
        }
    }

    override suspend fun addGroup(type: FavoriteType, songListId: List<Long>) {
        favoriteDao.addToFavorite(type, songListId)
        val songId = favoriteStatePublisher.value.songId
        if (songListId.contains(songId)) {
            updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type))
        }
    }

    override suspend fun deleteSingle(type: FavoriteType, songId: Long) {
        favoriteDao.removeFromFavorite(type, listOf(songId))
        val id = favoriteStatePublisher.value.songId
        if (songId == id) {
            updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
        }
    }

    override suspend fun deleteGroup(type: FavoriteType, songListId: List<Long>) {
        favoriteDao.removeFromFavorite(type, songListId)
        val songId = favoriteStatePublisher.value.songId
        if (songListId.contains(songId)) {
            updateFavoriteState(
                FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type)
            )
        }
    }

    override suspend fun deleteAll(type: FavoriteType) {
        when (type) {
            FavoriteType.TRACK -> favoriteDao.deleteTracks()
            FavoriteType.PODCAST -> favoriteDao.deleteAllPodcasts()
        }
        val songId = favoriteStatePublisher.value.songId
        updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
    }

    override suspend fun isFavorite(type: FavoriteType, songId: Long): Boolean = when (type) {
        FavoriteType.TRACK -> favoriteDao.isFavorite(songId) != null
        FavoriteType.PODCAST -> favoriteDao.isFavoritePodcast(songId) != null
    }

    override suspend fun toggleFavorite() {
        assertBackgroundThread()

        val value = favoriteStatePublisher.valueOrNull ?: return
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