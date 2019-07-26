package dev.olog.msc.domain.interactor.dialog

import dev.olog.core.MediaId
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
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

        val ids = getSongListByParamUseCase(mediaId).map { it.id }
        return favoriteGateway.addGroup(type, ids)
    }

    class Input(
        val mediaId: MediaId,
        val type: FavoriteType
    )

}