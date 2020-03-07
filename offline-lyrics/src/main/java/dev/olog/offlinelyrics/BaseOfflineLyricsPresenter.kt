package dev.olog.offlinelyrics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

private const val INTERVAL = 100L

abstract class BaseOfflineLyricsPresenter constructor(
    private val lyricsGateway: OfflineLyricsGateway,
    private val observeUseCase: ObserveOfflineLyricsUseCase,
    private val insertUseCase: InsertOfflineLyricsUseCase,
    private val schedulers: Schedulers

): CoroutineScope by MainScope() {

    private val matcher = "\\[\\d{2}:\\d{2}.\\d{2,3}\\](.)*".toRegex()

    private var insertLyricsJob by autoDisposeJob()
    private val currentTrackIdPublisher = ConflatedBroadcastChannel<Long>()
    private val syncAdjustmentPublisher = ConflatedBroadcastChannel<Long>(0)

    private val lyricsPublisher = ConflatedBroadcastChannel<Lyrics>()

    private var observeLyricsJob by autoDisposeJob()
    private var transformLyricsJob by autoDisposeJob()
    private var syncJob by autoDisposeJob()

    private var originalLyrics = MutableLiveData("")
    private val observedLyrics = MutableLiveData<Lyrics>()

    private var incrementJob by autoDisposeJob()
    private val currentProgress = MutableLiveData(0L)
    val observeCurrentProgress: LiveData<Long> = currentProgress

    fun onStart() {
        observeLyricsJob = currentTrackIdPublisher.asFlow()
            .flatMapLatest { id -> observeUseCase(id) }
            .onEach { onNextLyrics(it) }
            .flowOn(schedulers.cpu)
            .launchIn(this)

        transformLyricsJob = lyricsPublisher.asFlow()
            .onEach { observedLyrics.value = it }
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

    fun dispose() {
        cancel()
    }

    fun onStateChanged(isPlaying: Boolean, position: Int, speed: Float) {
        if (isPlaying) {
            startAutoIncrement(position, speed)
        } else {
            stopAutoIncrement()
        }
    }

    private fun startAutoIncrement(startMillis: Int, speed: Float) {
        stopAutoIncrement()
        incrementJob = flowInterval(
            INTERVAL,
            TimeUnit.MILLISECONDS
        )
            .map { (it + 1) * INTERVAL * speed + startMillis + syncAdjustmentPublisher.value }
            .flowOn(schedulers.io)
            .onEach { currentProgress.value = it.toLong() }
            .launchIn(this)
    }

    private fun stopAutoIncrement() {
        incrementJob = null
    }

    fun observeLyrics(): Flow<Lyrics> = observedLyrics.asFlow()

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
            lyricsPublisher.offer(Lyrics(listOf(OfflineLyricsLine(lyrics, 0L))))
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

                val textOnly = it.drop(10)

                OfflineLyricsLine(textOnly.trim(), time)
            }.fold(mutableListOf<OfflineLyricsLine>()) { acc, item ->
                if (acc.isEmpty()) {
                    acc.add(item)
                    return@fold acc
                }

                if (acc.last().value.isBlank() && item.value.isBlank()) {
                    // don't add new, previous item is already blank
                    return@fold acc
                }
                acc.add(item)
                acc
            }.map { item -> if (item.value.isBlank()) item.copy(value = "...") else item }

            lyricsPublisher.offer(Lyrics(result))
        }
    }

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.offer(trackId)
    }

    fun getLyrics(): String {
        return originalLyrics.value!!
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

}