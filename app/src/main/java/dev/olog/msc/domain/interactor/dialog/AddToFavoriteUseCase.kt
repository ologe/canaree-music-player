package dev.olog.msc.domain.interactor.dialog

import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.core.interactor.CompletableUseCaseWithParam
import dev.olog.core.MediaId
import dev.olog.shared.mapToList
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