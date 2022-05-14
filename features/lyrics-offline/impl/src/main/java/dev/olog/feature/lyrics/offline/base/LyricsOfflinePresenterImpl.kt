package dev.olog.feature.lyrics.offline.base

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.feature.lyrics.offline.api.Lyrics
import dev.olog.feature.lyrics.offline.api.LyricsOfflinePresenter
import dev.olog.shared.extension.dip
import dev.olog.shared.indexOfClosest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LyricsOfflinePresenterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lyricsGateway: OfflineLyricsGateway,
    private val observeUseCase: ObserveOfflineLyricsUseCase,
    private val insertUseCase: InsertOfflineLyricsUseCase
) : LyricsOfflinePresenter {

    companion object {
        const val DEFAULT_SPAN_SIZE_DP = 25
        const val CURRENT_SPAN_SIZE_DP = 30
    }

//    \[\d{2}:\d{2}.\d{2,3}\](.)*
    private val matcher = "\\[\\d{2}:\\d{2}.\\d{2,3}\\](.)*".toRegex()

    private val spannableBuilder = SpannableStringBuilder()

    private var insertLyricsJob: Job? = null
    private val currentTrackIdPublisher = ConflatedBroadcastChannel<Long>()
    private val syncAdjustmentPublisher = ConflatedBroadcastChannel<Long>(0)

    private val lyricsPublisher = ConflatedBroadcastChannel<Lyrics>()

    private var observeLyricsJob: Job? = null
    private var transformLyricsJob: Job? = null
    private var syncJob: Job? = null

    private val originalLyrics = MutableStateFlow<CharSequence?>(null)
    private val observedLyrics = MutableStateFlow<Pair<CharSequence, Lyrics>?>(null)

    private var currentStartMillis = -1
    private var currentSpeed = 1f

    private var tick = 0

    override var currentParagraph : Int = 0
        private set

    override fun onStart() {
        observeLyricsJob = GlobalScope.launch(Dispatchers.Default) {
            currentTrackIdPublisher.asFlow()
                .flatMapLatest { id -> observeUseCase(id) }
                .flowOn(Dispatchers.IO)
                .collect { onNextLyrics(it) }
        }
        transformLyricsJob = GlobalScope.launch {
            lyricsPublisher.asFlow()
                .flatMapLatest {
                    when (it) {
                        is Lyrics.Normal -> {
                            flowOf(it.lyrics to it)
                        }
                        is Lyrics.Synced -> {
                            handleSyncedLyrics(it)
                        }
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect {
                    withContext(Dispatchers.Main) {
                        observedLyrics.value = it
                    }
                }
        }
        syncJob = GlobalScope.launch {
            currentTrackIdPublisher.asFlow()
                .flatMapLatest { lyricsGateway.observeSyncAdjustment(it) }
                .collect { syncAdjustmentPublisher.trySend(it) }
        }
    }

    override fun onStop() {
        observeLyricsJob?.cancel()
        transformLyricsJob?.cancel()
        syncJob?.cancel()
    }

    override fun onStateChanged(position: Int, speed: Float) {
        currentStartMillis = position
        currentSpeed = speed
    }

    override fun resetTick() {
        tick = 0
    }

    override fun observeLyrics(): Flow<Pair<CharSequence, Lyrics>> = observedLyrics.filterNotNull()

    private suspend fun onNextLyrics(lyrics: String) {
        withContext(Dispatchers.Main) {
            originalLyrics.value = lyrics
        }
        // add a newline to ensure that last word is matched correctly
        val sanitizedString = lyrics.trim() + "\n"
        val matches = matcher.findAll(sanitizedString)
            .map { it.value.trim() }
            .toList()

        if (matches.isEmpty()) {
            // not synced
            lyricsPublisher.trySend(Lyrics.Normal(noSyncDefaultSpan(lyrics)))
        } else {
            // synced lyrics
            val result = matches.map {

                val minutes = TimeUnit.MINUTES.toMillis(
                    it[1].toString().toLong() * 10L +
                            it[2].toString().toLong()
                )
                val seconds = TimeUnit.SECONDS.toMillis(
                    it[4].toString().toLong() * 10L +
                            it[5].toString().toLong()
                )
                val millis = it[7].toString().toLong() * 100L + it[8].toString().toLong() * 10
                val time = minutes + seconds + millis

                val textOnly = it.substring(10)

                time to SpannableStringBuilder().apply {
                    append(textOnly)
                    defaultSpan(this, 0, textOnly.length)
                }
            }
            lyricsPublisher.trySend(Lyrics.Synced(result))
        }
    }

    private fun handleSyncedLyrics(syncedLyrics: Lyrics.Synced): Flow<Pair<Spannable, Lyrics>> {
        spannableBuilder.clear()
        val words = mutableListOf<Pair<Int, Int>>()
        for (lyric in syncedLyrics.lyrics) {
            words.add(spannableBuilder.length to spannableBuilder.length + lyric.second.length)
            spannableBuilder.append(lyric.second)
            spannableBuilder.appendln()
        }

        val interval = 300L

        return flow {
            delay(250L)
            emit(++tick)

            while (true) {
                delay(interval)
                if (currentSpeed == 0f) {
                    tick = 0
                }
                emit(++tick)
            }
        }.map {
            val current = currentStartMillis - syncAdjustmentPublisher.value + // static
                    (it + 1L) * interval * currentSpeed // dynamic

            syncedLyrics.lyrics
                .map { it.first }
                .indexOfClosest(current.toLong())
        }.distinctUntilChanged()
            .filter { it >= 0 }
            .map { closest ->
                currentParagraph = closest

                val (from, to) = words[closest]
                SpannableStringBuilder(spannableBuilder).apply {
                    currentSpan(this, from, to)
                } to syncedLyrics
            }
    }

    override fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.trySend(trackId)
    }

    override fun getLyrics(): String {
        return originalLyrics.value?.toString().orEmpty()
    }

    override fun updateSyncAdjustment(value: Long) {
        GlobalScope.launch {
            lyricsGateway.setSyncAdjustment(currentTrackIdPublisher.value, value)
        }
    }

    override suspend fun getSyncAdjustment(): String = withContext(Dispatchers.IO) {
        "${lyricsGateway.getSyncAdjustment(currentTrackIdPublisher.value)}"
    }

    override fun updateLyrics(lyrics: String) {
        if (currentTrackIdPublisher.valueOrNull == null) {
            return
        }
        insertLyricsJob?.cancel()
        insertLyricsJob = GlobalScope.launch {
            insertUseCase(OfflineLyrics(currentTrackIdPublisher.value, lyrics))
        }
    }

    private fun noSyncDefaultSpan(lyrics: String): Spannable {
        return SpannableStringBuilder(lyrics).apply {
            setSpan(
                ForegroundColorSpan(Color.WHITE),
                0,
                lyrics.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                AbsoluteSizeSpan(context.dip(DEFAULT_SPAN_SIZE_DP)),
                0,
                lyrics.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun defaultSpan(builder: SpannableStringBuilder, from: Int, to: Int) {
        builder.setSpan(
            ForegroundColorSpan(0xFF_757575.toInt()),
            from,
            to,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            AbsoluteSizeSpan(context.dip(DEFAULT_SPAN_SIZE_DP)),
            from,
            to,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun currentSpan(builder: SpannableStringBuilder, from: Int, to: Int) {
        builder.setSpan(
            ForegroundColorSpan(Color.WHITE),
            from,
            to,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            AbsoluteSizeSpan(context.dip(CURRENT_SPAN_SIZE_DP)),
            from,
            to,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            StyleSpan(Typeface.BOLD),
            from,
            to,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

}