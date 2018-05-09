package dev.olog.msc.domain.interactor.all

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
        gateway: SongGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Song>(gateway, schedulers)