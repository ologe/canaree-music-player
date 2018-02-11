package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import javax.inject.Inject

class GetAllFoldersUseCase @Inject constructor(
        gateway: FolderGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Folder>(gateway, schedulers)