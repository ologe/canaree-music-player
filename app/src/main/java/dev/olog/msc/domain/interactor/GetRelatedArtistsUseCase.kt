package dev.olog.msc.domain.interactor

import android.content.Context
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetRelatedArtistsUseCase @Inject constructor(
        @ApplicationContext private val context: Context,
        executors: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : ObservableUseCaseUseCaseWithParam<List<Artist>, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Artist>> {
        return getSongListByParamUseCase.execute(mediaId)
                .map { it.filter { it.artist != AppConstants.UNKNOWN }
                        .map { it.toArtist(context, -1, -1) }
                }
    }
}