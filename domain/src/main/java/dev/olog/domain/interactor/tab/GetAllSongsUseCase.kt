package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.SongGateway
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
        gateway: SongGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Song>(gateway, schedulers)