package dev.olog.msc.domain.interactor.dialog

import dev.olog.msc.domain.entity.FavoriteType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Completable
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val favoriteGateway: FavoriteGateway,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : CompletableUseCaseWithParam<AddToFavoriteUseCase.Input>(scheduler) {

    override fun buildUseCaseObservable(param: AddToFavoriteUseCase.Input): Completable {
        val mediaId = param.mediaId
        val type = param.type
        if (mediaId.isLeaf) {
            val songId = mediaId.leaf!!
            return favoriteGateway.addSingle(type, songId)
        }

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .mapToList { it.id }
                .flatMapCompletable { favoriteGateway.addGroup(type, it) }
    }

    class Input(
        val mediaId: MediaId,
        val type: FavoriteType
    )

}