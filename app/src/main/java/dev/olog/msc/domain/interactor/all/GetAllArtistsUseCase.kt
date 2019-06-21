package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.Artist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
        gateway: ArtistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Artist>(gateway, schedulers)