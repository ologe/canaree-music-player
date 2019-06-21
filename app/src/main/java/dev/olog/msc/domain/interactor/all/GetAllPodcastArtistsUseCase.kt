package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.PodcastArtist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.PodcastArtistGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastArtistsUseCase @Inject constructor(
        gateway: PodcastArtistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<PodcastArtist>(gateway, schedulers)