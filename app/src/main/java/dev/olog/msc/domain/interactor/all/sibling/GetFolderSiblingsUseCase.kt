package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.core.entity.track.Folder
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetFolderSiblingsUseCase @Inject constructor(
    schedulers: IoScheduler,
    private val gateway: FolderGateway

) : ObservableUseCaseWithParam<List<Folder>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId) : Observable<List<Folder>> {
        val folderPath = mediaId.categoryValue

        return gateway.getAll().map { it.filter { it.path != folderPath } }
    }
}
