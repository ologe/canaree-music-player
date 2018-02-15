package dev.olog.msc.data.repository

import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.domain.entity.AnimateFavoriteEntity
import dev.olog.msc.domain.entity.AnimateFavoriteEnum
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val songGateway: SongGateway

) : FavoriteGateway {

    private var lastFavoriteId : Long? = null

    private val favoriteDao = appDatabase.favoriteDao()

    private val toggleFavoritePublisher = PublishSubject.create<AnimateFavoriteEntity>()

    override fun getAll(): Observable<List<Song>> {
        return favoriteDao.getAllImpl()
                .toObservable()
                .flatMap { favorites -> songGateway.getAll().map { songList ->
                    favorites.mapNotNull { favoriteId -> songList.firstOrNull { it.id == favoriteId } }
                            .sortedBy { it.title.toLowerCase() }
                } }
    }

    override fun addSingle(songId: Long): Completable {
        return favoriteDao.addToFavoriteSingle(songId)
                .andThen { updateFavoriteState(songId) }
    }

    override fun addGroup(songListId: List<Long>): Completable {
        return favoriteDao.addToFavorite(songListId)
                .andThen { updateFavoriteState(lastFavoriteId!!) }
    }

    override fun deleteSingle(songId: Long): Completable {
        return favoriteDao.removeFromFavorite(listOf(songId))
                .toSingleDefault("")
                .flatMap { string ->
                    if (lastFavoriteId != null){
                        updateFavoriteState(lastFavoriteId!!).map { string }
                    } else Single.just(string)
                }.toCompletable()
    }

    override fun deleteGroup(songListId: List<Long>): Completable {
        return favoriteDao.removeFromFavorite(songListId)
                .toSingleDefault("")
                .flatMap { string ->
                    if (lastFavoriteId != null){
                        updateFavoriteState(lastFavoriteId!!).map { string }
                    } else Single.just(string)
                }.toCompletable()
    }

    override fun deleteAll(): Completable {
        return Completable.fromCallable { favoriteDao.deleteAll() }
                .andThen { handleAnimation(false) }
    }

    override fun isFavorite(songId: Long): Single<Boolean> {
        lastFavoriteId = songId
        return Single.fromCallable { favoriteDao.isFavorite(songId) != null }
    }

    override fun toggleLastFavorite() {
        lastFavoriteId?.let { toggleFavorite(it) }
    }

    override fun toggleFavorite(songId: Long) {

        Single.fromCallable { favoriteDao.isFavorite(songId) != null }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { isFavorite ->
                    toggleFavoritePublisher.onNext(AnimateFavoriteEntity(
                            if (isFavorite) AnimateFavoriteEnum.TO_NOT_FAVORITE
                            else AnimateFavoriteEnum.TO_FAVORITE
                    ))
                }
                .flatMapCompletable { isFavorite ->
                    if (isFavorite){
                        favoriteDao.removeFromFavorite(listOf(songId))
                    } else {
                        favoriteDao.addToFavorite(listOf(songId))
                    }
                }.subscribe({}, Throwable::printStackTrace)
    }

    private fun updateFavoriteState(songId: Long): Single<Boolean> {
        return isFavorite(songId).doOnSuccess(this::handleAnimation)
    }

    // todo bad
    private fun handleAnimation(isFavorite: Boolean){
        toggleFavoritePublisher.onNext(AnimateFavoriteEntity(
                if (isFavorite) AnimateFavoriteEnum.TO_FAVORITE
                else AnimateFavoriteEnum.TO_NOT_FAVORITE
        ))
    }

    override fun observeToggleFavorite(): Observable<AnimateFavoriteEntity> = toggleFavoritePublisher
}