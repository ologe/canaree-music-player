package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import javax.inject.Inject

class GetAllFoldersUseCase @Inject constructor(
        gateway: FolderGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Folder>(gateway, schedulers)