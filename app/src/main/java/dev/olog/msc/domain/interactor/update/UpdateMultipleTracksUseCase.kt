package dev.olog.msc.domain.interactor.update

import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.core.interactor.CompletableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Completable
import io.reactivex.Observable
import org.jaudiotagger.tag.FieldKey
import javax.inject.Inject

class UpdateMultipleTracksUseCase @Inject constructor(
        schedulers: IoSchedulers,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val updateTrackUseCase: UpdateTrackUseCase,
        private val gateway: UsedImageGateway

): CompletableUseCaseWithParam<UpdateMultipleTracksUseCase.Data>(schedulers){

    override fun buildUseCaseObservable(param: Data): Completable {
        return getSongListByParamUseCase.execute(param.mediaId)
                .firstOrError()
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapCompletable { updateTrackUseCase.execute(
                        UpdateTrackUseCase.Data(null, it.path, null, param.fields)
                ) }.andThen {
                    if (param.mediaId.isArtist || param.mediaId.isPodcastArtist){
                        gateway.setForArtist(param.mediaId.resolveId, param.image)
                    } else if (param.mediaId.isAlbum || param.mediaId.isPodcastAlbum){
                        gateway.setForAlbum(param.mediaId.resolveId, param.image)
                    } else {
                        throw IllegalStateException("invalid media id category ${param.mediaId}")
                    }
                }

    }

    data class Data(
        val mediaId: MediaId,
        val image: String?,
        val fields: Map<FieldKey, String>
    )

}