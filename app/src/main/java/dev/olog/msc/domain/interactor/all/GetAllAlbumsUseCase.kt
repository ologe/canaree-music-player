package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.Album
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllAlbumsUseCase @Inject constructor(
        gateway: AlbumGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Album>(gateway, schedulers)