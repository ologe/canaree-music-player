package dev.olog.offlinelyrics

import dev.olog.domain.entity.OfflineLyrics
import dev.olog.domain.gateway.OfflineLyricsGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.launchUnit
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

abstract class BaseOfflineLyricsPresenter constructor(
    private val lyricsGateway: OfflineLyricsGateway,
    private val observeUseCase: ObserveOfflineLyricsUseCase,
    private val insertUseCase: InsertOfflineLyricsUseCase,
    private val schedulers: Schedulers

): CoroutineScope by MainScope() {

    companion object {
        internal const val ELLIPSES = "..."
    }

    var firstEnter = true

    private val matcher = "\\[\\d{2}:\\d{2}.\\d{2,3}\\](.)*".toRegex()

    private var insertLyricsJob by autoDisposeJob()
    private val currentTrackIdPublisher = ConflatedBroadcastChannel<Long>()
    private val syncAdjustmentPublisher = ConflatedBroadcastChannel<Long>(0)

    private val lyricsPublisher = ConflatedBroadcastChannel<Lyrics>()

    private var observeLyricsJob by autoDisposeJob()
    private var syncJob by autoDisposeJob()

    private var originalLyricsPublisher = ConflatedBroadcastChannel("")

    fun onStart() {
        observeLyricsJob = currentTrackIdPublisher.asFlow()
            .flatMapLatest { id -> observeUseCase(id) }
            .onEach { onNextLyrics(it) }
            .flowOn(schedulers.cpu)
            .launchIn(this)

        syncJob = currentTrackIdPublisher.asFlow()
            .flatMapLatest { lyricsGateway.observeSyncAdjustment(it) }
            .onEach { syncAdjustmentPublisher.offer(it) }
            .flowOn(schedulers.cpu)
            .launchIn(this)
    }

    fun onStop() {
        observeLyricsJob = null
        syncJob = null
    }

    fun dispose() {
        currentTrackIdPublisher.close()
        lyricsPublisher.close()
        syncAdjustmentPublisher.close()
        originalLyricsPublisher.close()
        cancel()
    }

    fun observeLyrics(): Flow<Lyrics> = lyricsPublisher.asFlow()

    private fun onNextLyrics(lyrics: String) {
        originalLyricsPublisher.offer(lyrics)
        // add a newline to ensure that last word is matched correctly
        val sanitizedString = lyrics.trim() + "\n"
        val matches = matcher.findAll(sanitizedString)
            .map { it.value.trim() }
            .toList()

        if (matches.isEmpty()) {
            // not synced
            if (lyrics.isBlank()) {
                lyricsPublisher.offer(Lyrics(emptyList()))
            } else {
                lyricsPublisher.offer(Lyrics(listOf(OfflineLyricsLine(lyrics, 0L))))
            }
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
            }.map { item -> if (item.value.isBlank()) item.copy(value = ELLIPSES) else item }

            lyricsPublisher.offer(Lyrics(result))
        }
    }

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.offer(trackId)
    }

    fun getLyrics(): String {
        return originalLyricsPublisher.value
    }

    fun updateSyncAdjustment(value: Long) = GlobalScope.launchUnit(schedulers.io) {
        lyricsGateway.setSyncAdjustment(currentTrackIdPublisher.value, value)
    }

    suspend fun getSyncAdjustment(): String = withContext(schedulers.io) {
        "${lyricsGateway.getSyncAdjustment(currentTrackIdPublisher.value)}"
    }

    fun updateLyrics(lyrics: String) {
        insertLyricsJob = GlobalScope.launch(schedulers.io) {
            insertUseCase(OfflineLyrics(currentTrackIdPublisher.value, lyrics))
        }
    }

}