package dev.olog.feature.floating

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.feature.offline.lyrics.BaseOfflineLyricsPresenter
import dev.olog.feature.offline.lyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.feature.offline.lyrics.domain.ObserveOfflineLyricsUseCase
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