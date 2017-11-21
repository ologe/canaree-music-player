package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Folder
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import javax.inject.Inject

class GetAllFoldersUseCase @Inject constructor(
        gateway: FolderGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Folder>(gateway, schedulers)