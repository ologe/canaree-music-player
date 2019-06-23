package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.FolderGateway2
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetFolderSiblingsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: FolderGateway2

) : ObservableUseCaseWithParam<List<Folder>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Folder>> {
        val folderPath = mediaId.categoryValue

        return gateway.observeAll().map { it.filter { it.path != folderPath } }
                .asObservable()
    }
}
