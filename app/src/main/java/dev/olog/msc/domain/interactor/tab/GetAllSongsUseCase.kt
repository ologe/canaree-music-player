package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.SongGateway
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
        gateway: SongGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Song>(gateway, schedulers)