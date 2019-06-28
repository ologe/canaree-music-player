package dev.olog.service.floating

import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.offlinelyrics.BaseOfflineLyricsPresenter
import dev.olog.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import javax.inject.Inject

class OfflineLyricsContentPresenter @Inject constructor(
    appPreferencesUseCase: AppPreferencesGateway,
    observeUseCase: ObserveOfflineLyricsUseCase,
    insertUseCase: InsertOfflineLyricsUseCase

) : BaseOfflineLyricsPresenter(
    appPreferencesUseCase,
    observeUseCase,
    insertUseCase
)