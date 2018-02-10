package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
        gateway: PlaylistGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Playlist>(gateway, schedulers)