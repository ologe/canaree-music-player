package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.SingleUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val favoriteGateway: FavoriteGateway,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : SingleUseCaseWithParam<String, String>(scheduler) {

    override fun buildUseCaseObservable(param: String): Single<String> {
        val category = MediaIdHelper.extractCategory(param)

        if (MediaIdHelper.isSong(param) || category == MediaIdHelper.MEDIA_ID_BY_ALL) {
            val songId = MediaIdHelper.extractLeaf(param).toLong()
            return favoriteGateway.addSingle(songId)
        }

        return getSongListByParamUseCase.execute(param)
                .observeOn(Schedulers.io())
                .firstOrError()
                .flatMap { it.toFlowable()
                        .map { it.id }
                        .toList()
                }.flatMap{ favoriteGateway.addGroup(it) }
    }
}