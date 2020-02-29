package dev.olog.service.floating

import android.content.Context
import dev.olog.shared.ApplicationContext
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.offlinelyrics.BaseOfflineLyricsPresenter
import dev.olog.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import javax.inject.Inject

class OfflineLyricsContentPresenter @Inject constructor(
    @ApplicationContext context: Context,
    lyricsGateway: OfflineLyricsGateway,
    observeUseCase: ObserveOfflineLyricsUseCase,
    insertUseCase: InsertOfflineLyricsUseCase

) : BaseOfflineLyricsPresenter(
    context,
    lyricsGateway,
    observeUseCase,
    insertUseCase
)