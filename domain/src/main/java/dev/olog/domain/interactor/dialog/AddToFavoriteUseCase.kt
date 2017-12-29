package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.SingleUseCaseWithParam
import dev.olog.shared.MediaId
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