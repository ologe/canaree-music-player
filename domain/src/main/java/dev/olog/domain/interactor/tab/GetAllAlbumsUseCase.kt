package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.AlbumGateway
import javax.inject.Inject

class GetAllAlbumsUseCase @Inject constructor(
        gateway: AlbumGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Album>(gateway, schedulers)