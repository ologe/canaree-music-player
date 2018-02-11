package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
        gateway: PlaylistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Playlist>(gateway, schedulers)