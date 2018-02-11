package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
        gateway: ArtistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Artist>(gateway, schedulers)