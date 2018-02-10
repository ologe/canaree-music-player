package dev.olog.msc.domain.interactor.dialog

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.SingleUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val favoriteGateway: FavoriteGateway,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : SingleUseCaseWithParam<String, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Single<String> {

        if (mediaId.isAll || mediaId.isLeaf) {
            val songId = mediaId.leaf!!
            return favoriteGateway.addSingle(songId)
        }

        return getSongListByParamUseCase.execute(mediaId)
                .observeOn(Schedulers.io())
                .firstOrError()
                .flatMap { it.toFlowable()
                        .map { it.id }
                        .toList()
                }.flatMap{ favoriteGateway.addGroup(it) }
    }
}