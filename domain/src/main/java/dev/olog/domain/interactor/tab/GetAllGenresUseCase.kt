package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Genre
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.GenreGateway
import javax.inject.Inject

class GetAllGenresUseCase @Inject constructor(
        gateway: GenreGateway,
        schedulers: IoScheduler
) : GetGroupUseCase<Genre>(gateway, schedulers)