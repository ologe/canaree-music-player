package dev.olog.msc.floating.window.service

import dev.olog.msc.domain.interactor.offline.lyrics.InsertOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.offline.lyrics.ObserveOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.offline.lyrics.BaseOfflineLyricsPresenter
import javax.inject.Inject

class OfflineLyricsContentPresenter @Inject constructor(
        appPreferencesUseCase: AppPreferencesUseCase,
        observeUseCase: ObserveOfflineLyricsUseCase,
        insertUseCase: InsertOfflineLyricsUseCase

) : BaseOfflineLyricsPresenter(appPreferencesUseCase, observeUseCase, insertUseCase)