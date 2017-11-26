package dev.olog.domain.interactor.detail.most_played

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway

) : CompletableUseCaseWithParam<String>(scheduler) {

    override fun buildUseCaseObservable(param: String): Completable {
        return folderGateway.insertMostPlayed(param)
    }

    //    override fun buildUseCaseObservable(param: String): Flowable<List<Song>> {
//        val category = MediaIdHelper.extractCategory(param)
//        val categoryValue = MediaIdHelper.extractCategoryValue(param)
//        when (category) {
//            MediaIdHelper.MEDIA_ID_BY_GENRE -> return genreDataStore.observeSongListByParam(categoryValue.toLong())
//            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> return playlistDataStore.observeSongListByParam(categoryValue.toLong())
//            MediaIdHelper.MEDIA_ID_BY_FOLDER -> return fol.observeSongListByParam(categoryValue)
//            MediaIdHelper.MEDIA_ID_BY_ALBUM -> return albumDataStore.observeSongListByParam(categoryValue.toLong())
//            MediaIdHelper.MEDIA_ID_BY_ARTIST -> return artistDataStore.observeSongListByParam(categoryValue.toLong())
//            MediaIdHelper.MEDIA_ID_BY_ALL -> return songDataStore.getAll()
//        }
//        return folderGateway.getMostPlayed(param)
//    }
}