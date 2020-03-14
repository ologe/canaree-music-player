package dev.olog.data.repository

import dev.olog.core.entity.favorite.FavoriteState
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.data.db.FavoriteDao
import dev.olog.data.model.db.FavoriteEntity
import dev.olog.data.model.db.FavoritePodcastEntity
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val trackGateway: TrackGateway

) : FavoriteGateway {

    private val favoriteStatePublisher = ConflatedBroadcastChannel<dev.olog.core.entity.favorite.FavoriteEntity>()

    internal fun getState(): dev.olog.core.entity.favorite.FavoriteEntity? = favoriteStatePublisher.valueOrNull

    override fun observeToggleFavorite(): Flow<FavoriteState> = favoriteStatePublisher
        .asFlow()
        .map { it.enum }

    override suspend fun updateFavoriteState(state: dev.olog.core.entity.favorite.FavoriteEntity) {
        favoriteStatePublisher.offer(state)
    }

    override fun getTracks(): List<Song> {
        assertBackgroundThread()
        val favorites = favoriteDao.getAllTracksImpl()
        val songList: Map<Long, List<Song>> = trackGateway.getAllTracks().groupBy { it.id }
        return favorites.mapNotNull { id -> songList[id]?.get(0) }
    }

    override fun getPodcasts(): List<Song> {
        assertBackgroundThread()
        val favorites = favoriteDao.getAllPodcastsImpl()
        val podcastList: Map<Long, List<Song>> = trackGateway.getAllPodcasts().groupBy { it.id }
        return favorites.mapNotNull { id -> podcastList[id]?.get(0) }
    }

    override fun observeTracks(): Flow<List<Song>> {
        return favoriteDao.observeAllTracksImpl()
            .map { favorites ->
                val songs: Map<Long, List<Song>> = trackGateway.getAllTracks().groupBy { it.id }
                favorites.mapNotNull { id -> songs[id]?.get(0) }
                    .sortedBy { it.title }
            }
    }

    override fun observePodcasts(): Flow<List<Song>> {
        return favoriteDao.observeAllPodcastsImpl()
            .map { favorites ->
                val podcast: Map<Long, List<Song>> = trackGateway.getAllPodcasts().groupBy { it.id }
                favorites.mapNotNull { id -> podcast[id]?.get(0) }
                    .sortedBy { it.title }
            }
    }

    override suspend fun addSingle(type: FavoriteTrackType, songId: Long) {
        addToFavoriteSingle(type, songId)
        val id = favoriteStatePublisher.valueOrNull?.songId ?: return
        if (songId == id) {
            updateFavoriteState(
                dev.olog.core.entity.favorite.FavoriteEntity(songId, FavoriteState.FAVORITE, type)
            )
        }
    }

    override suspend fun addGroup(type: FavoriteTrackType, songListId: List<Long>) {
        addToFavorite(type, songListId)
        val songId = favoriteStatePublisher.valueOrNull?.songId ?: return
        if (songListId.contains(songId)) {
            updateFavoriteState(
                dev.olog.core.entity.favorite.FavoriteEntity(
                    songId = songId,
                    enum = FavoriteState.FAVORITE,
                    favoriteType = type
                )
            )
        }
    }

    override suspend fun deleteSingle(type: FavoriteTrackType, songId: Long) {
        removeFromFavorite(type, listOf(songId))
        val id = favoriteStatePublisher.valueOrNull?.songId ?: return
        if (songId == id) {
            updateFavoriteState(
                dev.olog.core.entity.favorite.FavoriteEntity(
                    songId = songId,
                    enum = FavoriteState.NOT_FAVORITE,
                    favoriteType = type
                )
            )
        }
    }

    override suspend fun deleteGroup(type: FavoriteTrackType, songListId: List<Long>) {
        removeFromFavorite(type, songListId)
        val songId = favoriteStatePublisher.valueOrNull?.songId ?: return
        if (songListId.contains(songId)) {
            updateFavoriteState(
                dev.olog.core.entity.favorite.FavoriteEntity(
                    songId = songId,
                    enum = FavoriteState.NOT_FAVORITE,
                    favoriteType = type
                )
            )
        }
    }

    override suspend fun deleteAll(type: FavoriteTrackType) {
        when (type) {
            FavoriteTrackType.TRACK -> favoriteDao.deleteAllTracks()
            FavoriteTrackType.PODCAST -> favoriteDao.deleteAllPodcasts()
        }

        val songId = favoriteStatePublisher.valueOrNull?.songId ?: return
        updateFavoriteState(
            dev.olog.core.entity.favorite.FavoriteEntity(
                songId = songId,
                enum = FavoriteState.NOT_FAVORITE,
                favoriteType = type
            )
        )
    }

    override suspend fun isFavorite(songId: Long, type: FavoriteTrackType): Boolean {
        return when (type){
            FavoriteTrackType.TRACK -> favoriteDao.isFavorite(songId)
            FavoriteTrackType.PODCAST -> favoriteDao.isFavoritePodcast(songId)
        }
    }

    override suspend fun toggleFavorite() {
        assertBackgroundThread()

        val value = favoriteStatePublisher.valueOrNull ?: return
        val id = value.songId
        val state = value.enum
        val type = value.favoriteType

        when (state) {
            FavoriteState.NOT_FAVORITE -> {
                updateFavoriteState(
                    dev.olog.core.entity.favorite.FavoriteEntity(
                        songId = id,
                        enum = FavoriteState.FAVORITE,
                        favoriteType = type
                    )
                )
                addToFavoriteSingle(type, id)
            }
            FavoriteState.FAVORITE -> {
                updateFavoriteState(
                    dev.olog.core.entity.favorite.FavoriteEntity(
                        songId = id,
                        enum = FavoriteState.NOT_FAVORITE,
                        favoriteType = type
                    )
                )
                removeFromFavorite(type, listOf(id))
            }
        }
    }

    private suspend fun addToFavoriteSingle(type: FavoriteTrackType, id: Long) {
        if (type == FavoriteTrackType.TRACK) {
            favoriteDao.insertOneImpl(FavoriteEntity(songId = id))
        } else {
            favoriteDao.insertOnePodcastImpl(
                FavoritePodcastEntity(podcastId = id)
            )
        }
    }

    private suspend fun addToFavorite(type: FavoriteTrackType, songIds: List<Long>) {
        if (type == FavoriteTrackType.TRACK) {
            favoriteDao.insertGroupImpl(songIds.map {
                FavoriteEntity(songId = it)
            })
        } else {
            favoriteDao.insertGroupPodcastImpl(songIds.map {
                FavoritePodcastEntity(podcastId = it)
            })
        }
    }

    private suspend fun removeFromFavorite(type: FavoriteTrackType, songId: List<Long>) {
        if (type == FavoriteTrackType.TRACK){
            favoriteDao.deleteGroupImpl(songId.map {
                FavoriteEntity(songId = it)
            })
        } else {
            favoriteDao.deleteGroupPodcastImpl(songId.map {
                FavoritePodcastEntity(podcastId = it)
            })
        }
    }

}