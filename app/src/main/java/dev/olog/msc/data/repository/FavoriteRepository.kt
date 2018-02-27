package dev.olog.msc.data.repository

import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.domain.entity.FavoriteEnum
import dev.olog.msc.domain.entity.FavoriteStateEntity
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val songGateway: SongGateway

) : FavoriteGateway {

    private val favoriteDao = appDatabase.favoriteDao()

    private val favoriteStatePublisher = BehaviorSubject.create<FavoriteStateEntity>()

    override fun observeToggleFavorite(): Observable<FavoriteEnum> = favoriteStatePublisher.map { it.enum }

    override fun updateFavoriteState(state: FavoriteStateEntity) {
        favoriteStatePublisher.onNext(state)
        if (state.enum == FavoriteEnum.ANIMATE_NOT_FAVORITE){
            favoriteStatePublisher.onNext(FavoriteStateEntity(state.songId, FavoriteEnum.NOT_FAVORITE))
        } else if (state.enum == FavoriteEnum.ANIMATE_TO_FAVORITE) {
            favoriteStatePublisher.onNext(FavoriteStateEntity(state.songId, FavoriteEnum.FAVORITE))
        }
    }

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
                .andThen({
                    val id = favoriteStatePublisher.value.songId
                    if (songId == id){
                        updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.FAVORITE))
                    }
                    it.onComplete()
                })
    }

    override fun addGroup(songListId: List<Long>): Completable {
        return favoriteDao.addToFavorite(songListId)
                .andThen({
                    val songId = favoriteStatePublisher.value.songId
                    if (songListId.contains(songId)){
                        updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.FAVORITE))
                    }
                    it.onComplete()
                })
    }

    override fun deleteSingle(songId: Long): Completable {
        return favoriteDao.removeFromFavorite(listOf(songId))
                .andThen({
                    val id = favoriteStatePublisher.value.songId
                    if (songId == id){
                        updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE))
                    }
                    it.onComplete()
                })
    }

    override fun deleteGroup(songListId: List<Long>): Completable {
        return favoriteDao.removeFromFavorite(songListId)
                .andThen({
                    val songId = favoriteStatePublisher.value.songId
                    if (songListId.contains(songId)){
                        updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE))
                    }
                    it.onComplete()
                })
    }

    override fun deleteAll(): Completable {
        return Completable.fromCallable { favoriteDao.deleteAll() }
                .andThen({
                    val songId = favoriteStatePublisher.value.songId
                    updateFavoriteState(FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE))
                    it.onComplete()
                })
    }

    override fun isFavorite(songId: Long): Single<Boolean> {
        return favoriteDao.isFavorite(songId)
                .map { true }
                .onErrorReturn { false }
    }

    override fun toggleFavorite(songId: Long) {
        val value = favoriteStatePublisher.value
        val id = value.songId
        val state = value.enum

        var action : Completable? = null

        if (state == FavoriteEnum.NOT_FAVORITE){
            updateFavoriteState(FavoriteStateEntity(id, FavoriteEnum.ANIMATE_TO_FAVORITE))
            action = favoriteDao.addToFavoriteSingle(songId)
        } else if (state == FavoriteEnum.FAVORITE){
            updateFavoriteState(FavoriteStateEntity(id, FavoriteEnum.ANIMATE_NOT_FAVORITE))
            action = favoriteDao.removeFromFavorite(listOf(id))
        } else {
            Completable.complete()
        }

        action?.subscribeOn(Schedulers.io())
                ?.subscribe({}, Throwable::printStackTrace)
    }
}