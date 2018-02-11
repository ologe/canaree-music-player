package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.SongGateway
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
        gateway: SongGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Song>(gateway, schedulers)