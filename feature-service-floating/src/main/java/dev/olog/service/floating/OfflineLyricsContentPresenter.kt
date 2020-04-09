package dev.olog.service.floating

import dev.olog.domain.gateway.OfflineLyricsGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.offline.lyrics.BaseOfflineLyricsPresenter
import dev.olog.lib.offline.lyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.lib.offline.lyrics.domain.ObserveOfflineLyricsUseCase
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