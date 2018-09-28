package dev.olog.msc.domain.interactor.all

import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.PodcastAlbumGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastAlbumsUseCase @Inject constructor(
        gateway: PodcastAlbumGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<PodcastAlbum>(gateway, schedulers)