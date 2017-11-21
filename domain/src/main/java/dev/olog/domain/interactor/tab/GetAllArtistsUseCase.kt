package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Artist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.ArtistGateway
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
        gateway: ArtistGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Artist>(gateway, schedulers)