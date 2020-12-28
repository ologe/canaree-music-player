package dev.olog.data.local.favorite

import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.PlaylistSong
import dev.olog.core.entity.track.Song
import dev.olog.core.entity.track.toPlaylistSong
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
) : FavoriteGateway {

    private val favoriteStatePublisher = MutableStateFlow(FavoriteStateEntity.INVALID)

    override fun observeToggleFavorite(): Flow<FavoriteEnum> = favoriteStatePublisher
        .filter { it != FavoriteStateEntity.INVALID }
        .map { it.enum }

    override suspend fun updateFavoriteState(state: FavoriteStateEntity) {
        favoriteStatePublisher.value = state
    }

    override suspend fun getTracks(): List<PlaylistSong> {
        val historyList = favoriteDao.getAllTracksImpl()
        val songList: Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
        return historyList.mapNotNull { id -> songList[id]?.get(0) }
            .mapIndexed { index, song -> song.toPlaylistSong(index.toLong()) }
    }

    override suspend fun getPodcasts(): List<PlaylistSong> {
        val historyList = favoriteDao.getAllPodcastsImpl()
        val songList: Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
        return historyList.mapNotNull { id -> songList[id]?.get(0) }
            .mapIndexed { index, song -> song.toPlaylistSong(index.toLong()) }
    }

    override fun observeTracks(): Flow<List<PlaylistSong>> {
        return favoriteDao.observeAllTracksImpl()
            .map { favorites ->
                val songs: Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
                favorites.mapNotNull { id -> songs[id]?.get(0) }
                    .mapIndexed { index, song -> song.toPlaylistSong(index.toLong()) }
                    .sortedBy { it.song.title }
            }
    }

    override fun observePodcasts(): Flow<List<PlaylistSong>> {
        return favoriteDao.observeAllPodcastsImpl()
            .map { favorites ->
                val podcast: Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
                favorites.mapNotNull { id -> podcast[id]?.get(0) }
                    .mapIndexed { index, song -> song.toPlaylistSong(index.toLong()) }
                    .sortedBy { it.song.title }
            }
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
        favoriteDao.deleteTracks()
        val songId = favoriteStatePublisher.value.songId
        updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
    }

    override suspend fun isFavorite(type: FavoriteType, songId: Long): Boolean {
        return favoriteDao.isFavorite(songId) != null
    }

    override suspend fun toggleFavorite() {
        val value = favoriteStatePublisher.value
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