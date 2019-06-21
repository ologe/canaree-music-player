package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.Playlist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
        gateway: PlaylistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Playlist>(gateway, schedulers)