package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
        gateway: ArtistGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Artist>(gateway, schedulers)