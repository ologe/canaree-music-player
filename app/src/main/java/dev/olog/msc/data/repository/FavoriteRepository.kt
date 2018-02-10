package dev.olog.msc.data.repository

import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.domain.entity.AnimateFavoriteEntity
import dev.olog.msc.domain.entity.AnimateFavoriteEnum
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.processors.PublishProcessor
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val songGateway: SongGateway

) : FavoriteGateway {

    private var lastFavoriteId : Long? = null

    private val favoriteDao = appDatabase.favoriteDao()

    private val toggleFavoritePublisher = PublishProcessor.create<AnimateFavoriteEntity>()

    override fun getAll(): Flowable<List<Song>> = favoriteDao.getAllImpl()
            .subscribeOn(Schedulers.io())
            .flatMapSingle { it.toFlowable()
                    .flatMapMaybe { songId ->
                        songGateway.getAll().flatMapIterable { it }
                                .filter { (id) -> id == songId }
                                .firstElement()
                    }.toSortedList { o1, o2 -> String.CASE_INSENSITIVE_ORDER.compare(o1.title, o2.title) }

            }

    override fun addSingle(songId: Long): Single<String> {
        return songGateway.getByParam(songId)
                .firstOrError()
                .flatMap { favoriteDao.addToFavoriteSingle(it) }
                .flatMap { string ->
                    updateFavoriteState(songId).map { string }
                }
    }

    override fun addGroup(songListId: List<Long>): Single<String> {
        return favoriteDao.addToFavorite(songListId)
                .flatMap { string ->
                    if (lastFavoriteId != null){
                        updateFavoriteState(lastFavoriteId!!).map { string }
                    } else Single.just(string)
                }
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
                                .toCompletable()
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

    override fun observeToggleFavorite(): Flowable<AnimateFavoriteEntity> = toggleFavoritePublisher
}