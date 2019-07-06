package dev.olog.data.repository

import android.annotation.SuppressLint
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.db.dao.AppDatabase
import dev.olog.shared.extensions.assertBackground
import dev.olog.shared.utils.assertBackgroundThread
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : FavoriteGateway {

    private val favoriteDao = appDatabase.favoriteDao()

    private val favoriteStatePublisher = BehaviorSubject.create<FavoriteStateEntity>()

    override fun observeToggleFavorite(): Observable<FavoriteEnum> =
        favoriteStatePublisher.map { it.enum }

    override fun updateFavoriteState(state: FavoriteStateEntity) {
        favoriteStatePublisher.onNext(state)
    }

    override fun getTracks(): List<Song> {
        assertBackgroundThread()
        val historyList = favoriteDao.getAllTracksImpl()
        val songList : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
        return historyList.mapNotNull { id -> songList[id]?.get(0) }
    }

    override fun getPodcasts(): List<Song> {
        assertBackgroundThread()
        val historyList = favoriteDao.getAllPodcastsImpl()
        val songList : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
        return historyList.mapNotNull { id -> songList[id]?.get(0) }
    }

    override fun observeTracks(): Flow<List<Song>> {
        return favoriteDao.observeAllTracksImpl()
            .asFlow()
            .map { favorites ->
                val songs : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
                favorites.mapNotNull { id -> songs[id]?.get(0) }
                    .sortedBy { it.title }
            }.assertBackground()
    }

    override fun observePodcasts(): Flow<List<Song>> {
        return favoriteDao.observeAllPodcastsImpl()
            .asFlow()
            .map { favorites ->
                val podcast : Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
                favorites.mapNotNull { id -> podcast[id]?.get(0) }
                    .sortedBy { it.title }
            }.assertBackground()
    }

    override fun addSingle(type: FavoriteType, songId: Long): Completable {
        return favoriteDao.addToFavoriteSingle(type, songId)
            .andThen {
                val id = favoriteStatePublisher.value?.songId ?: return@andThen
                if (songId == id) {
                    updateFavoriteState(
                        FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type)
                    )
                }
                it.onComplete()
            }
    }

    override fun addGroup(type: FavoriteType, songListId: List<Long>): Completable {
        return favoriteDao.addToFavorite(type, songListId)
            .andThen {
                val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                if (songListId.contains(songId)) {
                    updateFavoriteState(
                        FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type)
                    )
                }
                it.onComplete()
            }
    }

    override fun deleteSingle(type: FavoriteType, songId: Long): Completable {
        return favoriteDao.removeFromFavorite(type, listOf(songId))
            .andThen {
                val id = favoriteStatePublisher.value?.songId ?: return@andThen
                if (songId == id) {
                    updateFavoriteState(
                        FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type)
                    )
                }
                it.onComplete()
            }
    }

    override fun deleteGroup(type: FavoriteType, songListId: List<Long>): Completable {
        return favoriteDao.removeFromFavorite(type, songListId)
            .andThen {
                val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                if (songListId.contains(songId)) {
                    updateFavoriteState(
                        FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type)
                    )
                }
                it.onComplete()
            }
    }

    override fun deleteAll(type: FavoriteType): Completable {
        return Completable.fromCallable { favoriteDao.deleteTracks() }
            .andThen {
                val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                updateFavoriteState(
                    FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type)
                )
                it.onComplete()
            }
    }

    override fun isFavorite(type: FavoriteType, songId: Long): Single<Boolean> {
        return Single.fromCallable { favoriteDao.isFavorite(songId) != null }
    }

    // leaks for very small amount of time
    @SuppressLint("RxLeakedSubscription")
    override fun toggleFavorite() {
        val value = favoriteStatePublisher.value ?: return
        val id = value.songId
        val state = value.enum
        val type = value.favoriteType

        var action: Completable? = null

        when (state) {
            FavoriteEnum.NOT_FAVORITE -> {
                updateFavoriteState(
                    FavoriteStateEntity(id, FavoriteEnum.FAVORITE, type)
                )
                action = favoriteDao.addToFavoriteSingle(type, id)
            }
            FavoriteEnum.FAVORITE -> {
                updateFavoriteState(
                    FavoriteStateEntity(id, FavoriteEnum.NOT_FAVORITE, type)
                )
                action = favoriteDao.removeFromFavorite(type, listOf(id))
            }
            else -> Completable.complete()
        }

        action?.subscribeOn(Schedulers.io())
            ?.subscribe({}, Throwable::printStackTrace)
    }
}