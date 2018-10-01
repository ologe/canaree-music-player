package dev.olog.msc.data.repository

import android.annotation.SuppressLint
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.domain.entity.*
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.text.Collator
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val collator: Collator

) : FavoriteGateway {

    private val favoriteDao = appDatabase.favoriteDao()

    private val favoriteStatePublisher = BehaviorSubject.create<FavoriteStateEntity>()

    override fun observeToggleFavorite(): Observable<FavoriteEnum> = favoriteStatePublisher.map { it.enum }

    override fun updateFavoriteState(type: FavoriteType, state: FavoriteStateEntity) {
        favoriteStatePublisher.onNext(state)
        if (state.enum == FavoriteEnum.ANIMATE_NOT_FAVORITE){
            favoriteStatePublisher.onNext(FavoriteStateEntity(state.songId, FavoriteEnum.NOT_FAVORITE, type))
        } else if (state.enum == FavoriteEnum.ANIMATE_TO_FAVORITE) {
            favoriteStatePublisher.onNext(FavoriteStateEntity(state.songId, FavoriteEnum.FAVORITE, type))
        }
    }

    override fun getAll(): Observable<List<Song>> {
        return favoriteDao.getAllImpl()
                .toObservable()
                .switchMap { favorites -> songGateway.getAll().map { songList ->
                    favorites.mapNotNull { favoriteId -> songList.firstOrNull { it.id == favoriteId } }
                            .sortedWith(Comparator { o1, o2 -> collator.compare(o1.title, o2.title) })
                } }
    }

    override fun getAllPodcasts(): Observable<List<Podcast>> {
        return favoriteDao.getAllImpl()
                .toObservable()
                .switchMap { favorites -> podcastGateway.getAll().map { podcastList ->
                    favorites.mapNotNull { favoriteId -> podcastList.firstOrNull { it.id == favoriteId } }
                            .sortedWith(Comparator { o1, o2 -> collator.compare(o1.title, o2.title) })
                } }
    }

    override fun addSingle(type: FavoriteType, songId: Long): Completable {
        return favoriteDao.addToFavoriteSingle(type, songId)
                .andThen {
                    val id = favoriteStatePublisher.value?.songId ?: return@andThen
                    if (songId == id){
                        updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type))
                    }
                    it.onComplete()
                }
    }

    override fun addGroup(type: FavoriteType, songListId: List<Long>): Completable {
        return favoriteDao.addToFavorite(type, songListId)
                .andThen {
                    val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                    if (songListId.contains(songId)){
                        updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type))
                    }
                    it.onComplete()
                }
    }

    override fun deleteSingle(type: FavoriteType, songId: Long): Completable {
        return favoriteDao.removeFromFavorite(type, listOf(songId))
                .andThen {
                    val id = favoriteStatePublisher.value?.songId ?: return@andThen
                    if (songId == id){
                        updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
                    }
                    it.onComplete()
                }
    }

    override fun deleteGroup(type: FavoriteType, songListId: List<Long>): Completable {
        return favoriteDao.removeFromFavorite(type, songListId)
                .andThen {
                    val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                    if (songListId.contains(songId)){
                        updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
                    }
                    it.onComplete()
                }
    }

    override fun deleteAll(type: FavoriteType): Completable {
        return Completable.fromCallable { favoriteDao.deleteAll() }
                .andThen {
                    val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                    updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
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

        var action : Completable? = null

        when (state) {
            FavoriteEnum.NOT_FAVORITE -> {
                updateFavoriteState(type, FavoriteStateEntity(id, FavoriteEnum.ANIMATE_TO_FAVORITE, type))
                action = favoriteDao.addToFavoriteSingle(type, id)
            }
            FavoriteEnum.FAVORITE -> {
                updateFavoriteState(type, FavoriteStateEntity(id, FavoriteEnum.ANIMATE_NOT_FAVORITE, type))
                action = favoriteDao.removeFromFavorite(type, listOf(id))
            }
            else -> Completable.complete()
        }

        action?.subscribeOn(Schedulers.io())
                ?.subscribe({}, Throwable::printStackTrace)
    }
}