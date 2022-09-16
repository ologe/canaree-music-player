package dev.olog.data.favourites

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.PlayingGateway
import dev.olog.data.mediastore.podcast.toDomain
import dev.olog.data.mediastore.song.toDomain
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val playingGateway: PlayingGateway,
) : FavoriteGateway {

    override fun observePlayingFavorite(): Flow<Boolean> {
        return playingGateway.observe()
            .filterNotNull()
            .flatMapLatest { favoriteDao.isFavourite(it.id.toString()) }
            .map { it != null }
    }

    override suspend fun updateFavoriteState(id: String, isFavourite: Boolean) {
        if (isFavourite) {
            favoriteDao.insert(FavoriteEntity(id))
        } else {
            favoriteDao.delete(FavoriteEntity(id))
        }
    }

    override fun getTracks(): List<Song> {
        return favoriteDao.getAllTracks().map { it.toDomain() }
    }

    override fun getTracksCount(): Int {
        return favoriteDao.countAllSongs()
    }

    override fun getPodcasts(): List<Song> {
        return favoriteDao.getAllPodcasts().map { it.toDomain() }
    }

    override fun getPodcastsCount(): Int {
        return favoriteDao.countAllPodcasts()
    }

    override fun observeTracks(): Flow<List<Song>> {
        return favoriteDao.observeAllTracks()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeTracksCount(): Flow<Int> {
        return favoriteDao.observeCountAllSongs().distinctUntilChanged()
    }

    override fun observePodcasts(): Flow<List<Song>> {
        return favoriteDao.observeAllPodcasts()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observePodcastsCount(): Flow<Int> {
        return favoriteDao.observeCountAllPodcasts().distinctUntilChanged()
    }

    override suspend fun addSingle(id: Long) {
        favoriteDao.insert(FavoriteEntity(id.toString()))
    }

    override suspend fun addGroup(ids: List<Long>) {
        favoriteDao.insert(ids.map { FavoriteEntity(it.toString()) })
    }

    override suspend fun deleteSingle(id: Long) {
        favoriteDao.delete(FavoriteEntity(id.toString()))
    }

    override suspend fun deleteGroup(ids: List<Long>) {
        favoriteDao.delete(ids.map { FavoriteEntity(it.toString()) })
    }

    override suspend fun deleteAll(isPodcast: Boolean) {
        if (isPodcast) {
            favoriteDao.deleteAllTracks()
        } else {
            favoriteDao.deleteAllPodcasts()
        }
    }

    override suspend fun isFavorite(id: Long): Boolean {
        return favoriteDao.isFavourite(id.toString()).first() != null
    }

    override suspend fun toggleFavorite() {
        val playing = playingGateway.observe().first() ?: return
        val current = favoriteDao.isFavourite(playing.id.toString()).first()
        if (current != null) {
            favoriteDao.delete(current)
        } else {
            favoriteDao.insert(FavoriteEntity(playing.id.toString()))
        }
    }
}