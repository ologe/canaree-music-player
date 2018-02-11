package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import javax.inject.Inject

class GetAllAlbumsUseCase @Inject constructor(
        gateway: AlbumGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Album>(gateway, schedulers)