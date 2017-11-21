package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Playlist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
        gateway: PlaylistGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Playlist>(gateway, schedulers)