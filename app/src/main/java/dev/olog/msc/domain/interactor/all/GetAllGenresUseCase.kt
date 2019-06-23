package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.track.Genre
import dev.olog.core.executor.ComputationScheduler
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllGenresUseCase @Inject constructor(
        gateway: GenreGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Genre>(gateway, schedulers)