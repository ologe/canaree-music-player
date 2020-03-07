package dev.olog.service.floating

import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.offlinelyrics.BaseOfflineLyricsPresenter
import dev.olog.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import javax.inject.Inject

class OfflineLyricsContentPresenter @Inject constructor(
    lyricsGateway: OfflineLyricsGateway,
    observeUseCase: ObserveOfflineLyricsUseCase,
    insertUseCase: InsertOfflineLyricsUseCase,
    schedulers: Schedulers

) : BaseOfflineLyricsPresenter(
    lyricsGateway,
    observeUseCase,
    insertUseCase,
    schedulers
)