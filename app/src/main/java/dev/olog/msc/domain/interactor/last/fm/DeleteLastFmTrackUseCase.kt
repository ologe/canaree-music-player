package dev.olog.msc.domain.interactor.last.fm

import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

//class DeleteLastFmTrackUseCase @Inject constructor(
//        private val gateway: LastFmGateway
//
//) : WorkerUseCaseWithParam() {
//
//    companion object {
//        private const val TAG = "DeleteLastFmTrackUseCase"
//        const val TRACK_ID = "$TAG.track"
//    }
//
//    override fun buildUseCase(input: Data): Worker.WorkerResult {
//        val id = input.getLong(TRACK_ID, -1)
//
//        gateway.deleteTrack(id)
//
//        return Worker.WorkerResult.SUCCESS
//    }
//
//}

class DeleteLastFmTrackUseCase @Inject constructor(
        schedulers: IoSchedulers,
        private val gateway: LastFmGateway

): CompletableUseCaseWithParam<Long>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(trackId: Long): Completable {
        return Completable.fromCallable { gateway.deleteTrack(trackId) }
    }
}