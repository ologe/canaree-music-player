package dev.olog.msc.domain.interactor.update

import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import io.reactivex.Observable
import org.jaudiotagger.tag.FieldKey
import javax.inject.Inject

class UpdateMultipleTracksUseCase @Inject constructor(
        schedulers: IoSchedulers,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val updateTrackUseCase: UpdateTrackUseCase

): CompletableUseCaseWithParam<UpdateMultipleTracksUseCase.Data>(schedulers){

    override fun buildUseCaseObservable(param: Data): Completable {
        return getSongListByParamUseCase.execute(param.mediaId)
                .firstOrError()
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapCompletable { updateTrackUseCase.execute(
                        UpdateTrackUseCase.Data(it.path, param.fields)
                ) }

    }

    data class Data(
            val mediaId: MediaId,
            val fields: Map<FieldKey, String>
    )

}