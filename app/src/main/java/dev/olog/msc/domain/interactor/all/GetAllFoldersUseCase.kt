package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.track.Folder
import dev.olog.core.executor.ComputationScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllFoldersUseCase @Inject constructor(
        gateway: FolderGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Folder>(gateway, schedulers)