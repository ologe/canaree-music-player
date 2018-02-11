package dev.olog.msc.domain.interactor

import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetRelatedArtistsUseCase @Inject constructor(
        executors: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : ObservableUseCaseUseCaseWithParam<String, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<String> {
        return getSongListByParamUseCase.execute(mediaId)
                .map {
                    if (!mediaId.isAlbum && !mediaId.isArtist){
                        it.asSequence()
                                .filter { it.artist != AppConstants.UNKNOWN }
                                .distinct()
                                .map { it.artist }
                                .joinToString()
                    } else ""
                }
    }
}