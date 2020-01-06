package dev.olog.data.repository

import androidx.annotation.VisibleForTesting
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.db.FavoriteDao
import dev.olog.data.model.db.FavoriteEntity
import dev.olog.data.model.db.FavoritePodcastEntity
import dev.olog.data.utils.assertBackground
import dev.olog.data.utils.assertBackgroundThread
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : FavoriteGateway {

    private val favoriteStatePublisher = ConflatedBroadcastChannel<FavoriteStateEntity>()

    @VisibleForTesting
    internal fun getState(): FavoriteStateEntity? = favoriteStatePublisher.valueOrNull

    override fun observeToggleFavorite(): Flow<FavoriteEnum> = favoriteStatePublisher
        .asFlow()
        .map { it.enum }

    override suspend fun updateFavoriteState(state: FavoriteStateEntity) {
        favoriteStatePublisher.offer(state)
    }

    override fun getTracks(): List<Song> {
        assertBackgroundThread()
        val favorites = favoriteDao.getAllTracksImpl()
        val songList: Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
        return favorites.mapNotNull { id -> songList[id]?.get(0) }
    }

    override fun getPodcasts(): List<Song> {
        assertBackgroundThread()
        val favorites = favoriteDao.getAllPodcastsImpl()
        val podcastList: Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
        return favorites.mapNotNull { id -> podcastList[id]?.get(0) }
    }

    override fun observeTracks(): Flow<List<Song>> {
        return favoriteDao.observeAllTracksImpl()
            .map { favorites ->
                val songs: Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
                favorites.mapNotNull { id -> songs[id]?.get(0) }
                    .sortedBy { it.title }
            }.assertBackground()
    }

    override fun observePodcasts(): Flow<List<Song>> {
        return favoriteDao.observeAllPodcastsImpl()
            .map { favorites ->
                val podcast: Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
                favorites.mapNotNull { id -> podcast[id]?.get(0) }
                    .sortedBy { it.title }
            }.assertBackground()
    }

    override suspend fun addSingle(type: FavoriteType, songId: Long) {
        addToFavoriteSingle(type, songId)
        val id = favoriteStatePublisher.valueOrNull?.songId ?: return
        if (songId == id) {
            updateFavoriteState(
                FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type)
            )
        }
    }

    override suspend fun addGroup(type: FavoriteType, songListId: List<Long>) {
        addToFavorite(type, songListId)
        val songId = favoriteStatePublisher.valueOrNull?.songId ?: return
        if (songListId.contains(songId)) {
            updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type))
        }
    }

    override suspend fun deleteSingle(type: FavoriteType, songId: Long) {
        removeFromFavorite(type, listOf(songId))
        val id = favoriteStatePublisher.valueOrNull?.songId ?: return
        if (songId == id) {
            updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
        }
    }

    override suspend fun deleteGroup(type: FavoriteType, songListId: List<Long>) {
        removeFromFavorite(type, songListId)
        val songId = favoriteStatePublisher.valueOrNull?.songId ?: return
        if (songListId.contains(songId)) {
            updateFavoriteState(
                FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type)
            )
        }
    }

    override suspend fun deleteAll(type: FavoriteType) {
        when (type) {
            FavoriteType.TRACK -> favoriteDao.deleteAllTracks()
            FavoriteType.PODCAST -> favoriteDao.deleteAllPodcasts()
        }

        val songId = favoriteStatePublisher.valueOrNull?.songId ?: return
        updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
    }

    override suspend fun isFavorite(songId: Long, type: FavoriteType): Boolean {
        return when (type){
            FavoriteType.TRACK -> favoriteDao.isFavorite(songId)
            FavoriteType.PODCAST -> favoriteDao.isFavoritePodcast(songId)
        }
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
                addToFavoriteSingle(type, id)
            }
            FavoriteEnum.FAVORITE -> {
                updateFavoriteState(
                    FavoriteStateEntity(id, FavoriteEnum.NOT_FAVORITE, type)
                )
                removeFromFavorite(type, listOf(id))
            }
        }
    }

    private suspend fun addToFavoriteSingle(type: FavoriteType, id: Long) {
        if (type == FavoriteType.TRACK) {
            favoriteDao.insertOneImpl(FavoriteEntity(id))
        } else {
            favoriteDao.insertOnePodcastImpl(
                FavoritePodcastEntity(
                    id
                )
            )
        }
    }

    private suspend fun addToFavorite(type: FavoriteType, songIds: List<Long>) {
        if (type == FavoriteType.TRACK) {
            favoriteDao.insertGroupImpl(songIds.map {
                FavoriteEntity(
                    it
                )
            })
        } else {
            favoriteDao.insertGroupPodcastImpl(songIds.map {
                FavoritePodcastEntity(
                    it
                )
            })
        }
    }

    private suspend fun removeFromFavorite(type: FavoriteType, songId: List<Long>) {
        if (type == FavoriteType.TRACK){
            favoriteDao.deleteGroupImpl(songId.map {
                FavoriteEntity(
                    it
                )
            })
        } else {
            favoriteDao.deleteGroupPodcastImpl(songId.map {
                FavoritePodcastEntity(
                    it
                )
            })
        }
    }

}