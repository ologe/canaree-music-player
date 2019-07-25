package dev.olog.msc.domain.interactor.dialog

import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.MediaId
import dev.olog.shared.android.extensions.mapToList
import io.reactivex.Completable
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val favoriteGateway: FavoriteGateway,
        private val getSongListByParamUseCase: ObserveSongListByParamUseCase

) : CompletableUseCaseWithParam<AddToFavoriteUseCase.Input>(scheduler) {

    override fun buildUseCaseObservable(param: AddToFavoriteUseCase.Input): Completable {
        val mediaId = param.mediaId
        val type = param.type
        if (mediaId.isLeaf) {
            val songId = mediaId.leaf!!
            return favoriteGateway.addSingle(type, songId)
        }

        return getSongListByParamUseCase(mediaId)
                .asFlowable()
                .firstOrError()
                .mapToList { it.id }
                .flatMapCompletable { favoriteGateway.addGroup(type, it) }
    }

    class Input(
        val mediaId: MediaId,
        val type: FavoriteType
    )

}