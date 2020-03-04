package dev.olog.offlinelyrics

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import dev.olog.shared.android.extensions.dpToPx
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.indexOfClosest
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

sealed class Lyrics {
    class Normal(val lyrics: Spannable) : Lyrics()
    class Synced(val lyrics: List<Pair<Long, Spannable>>) : Lyrics()
}

abstract class BaseOfflineLyricsPresenter constructor(
    private val context: Context,
    private val lyricsGateway: OfflineLyricsGateway,
    private val observeUseCase: ObserveOfflineLyricsUseCase,
    private val insertUseCase: InsertOfflineLyricsUseCase,
    private val schedulers: Schedulers

): CoroutineScope by MainScope() {

    companion object {
        const val DEFAULT_SPAN_SIZE_DP = 25f
        const val CURRENT_SPAN_SIZE_DP = 30f
    }

//    \[\d{2}:\d{2}.\d{2,3}\](.)*
    private val matcher = "\\[\\d{2}:\\d{2}.\\d{2,3}\\](.)*".toRegex()

    private val spannableBuilder = SpannableStringBuilder()

    private var insertLyricsJob by autoDisposeJob()
    private val currentTrackIdPublisher = ConflatedBroadcastChannel<Long>()
    private val syncAdjustmentPublisher = ConflatedBroadcastChannel<Long>(0)

    private val lyricsPublisher = ConflatedBroadcastChannel<Lyrics>()

    private var observeLyricsJob by autoDisposeJob()
    private var transformLyricsJob by autoDisposeJob()
    private var syncJob by autoDisposeJob()

    private var originalLyrics = MutableLiveData<CharSequence>()
    private val observedLyrics = MutableLiveData<Pair<CharSequence, Lyrics>>()

    private var currentStartMillis = -1
    private var currentSpeed = 1f

    private var tick = 0

    var currentParagraph : Int = 0
        private set

    fun onStart() {
        observeLyricsJob = currentTrackIdPublisher.asFlow()
            .flatMapLatest { id -> observeUseCase(id) }
            .onEach { onNextLyrics(it) }
            .flowOn(schedulers.cpu)
            .launchIn(this)

        transformLyricsJob = lyricsPublisher.asFlow()
            .flatMapLatest {
                when (it) {
                    is Lyrics.Normal -> flowOf(it.lyrics to it)
                    is Lyrics.Synced -> handleSyncedLyrics(it)
                }
            }
            .flowOn(schedulers.cpu)
            .onEach { observedLyrics.value = it }
            .flowOn(schedulers.main)
            .launchIn(this)

        syncJob = currentTrackIdPublisher.asFlow()
            .flatMapLatest { lyricsGateway.observeSyncAdjustment(it) }
            .onEach { syncAdjustmentPublisher.offer(it) }
            .flowOn(schedulers.cpu)
            .launchIn(this)
    }

    fun onStop() {
        observeLyricsJob = null
        transformLyricsJob = null
        syncJob = null
    }

    fun onStateChanged(position: Int, speed: Float) {
        currentStartMillis = position
        currentSpeed = speed
    }

    fun resetTick(){
        tick = 0
    }

    fun observeLyrics(): LiveData<Pair<CharSequence, Lyrics>> = observedLyrics

    private suspend fun onNextLyrics(lyrics: String) {
        withContext(schedulers.main) {
            originalLyrics.value = lyrics
        }
        // add a newline to ensure that last word is matched correctly
        val sanitizedString = lyrics.trim() + "\n"
        val matches = matcher.findAll(sanitizedString)
            .map { it.value.trim() }
            .toList()

        if (matches.isEmpty()) {
            // not synced
            lyricsPublisher.offer(Lyrics.Normal(noSyncDefaultSpan(lyrics)))
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
            lyricsPublisher.offer(Lyrics.Synced(result))
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

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.offer(trackId)
    }

    fun getLyrics(): String {
        return originalLyrics.value.toString()
    }

    fun updateSyncAdjustment(value: Long) {
        GlobalScope.launch {
            lyricsGateway.setSyncAdjustment(currentTrackIdPublisher.value, value)
        }
    }

    suspend fun getSyncAdjustment(): String = withContext(schedulers.io) {
        "${lyricsGateway.getSyncAdjustment(currentTrackIdPublisher.value)}"
    }

    fun updateLyrics(lyrics: String) {
        if (currentTrackIdPublisher.valueOrNull == null) {
            return
        }
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
                AbsoluteSizeSpan(context.dpToPx(DEFAULT_SPAN_SIZE_DP)),
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
            AbsoluteSizeSpan(context.dpToPx(DEFAULT_SPAN_SIZE_DP)),
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
            AbsoluteSizeSpan(context.dpToPx(CURRENT_SPAN_SIZE_DP)),
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