package dev.olog.offlinelyrics

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import dev.olog.offlinelyrics.model.LyricsModel
import dev.olog.shared.android.extensions.dpToPx
import dev.olog.shared.clamp
import dev.olog.shared.flowInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

abstract class BaseOfflineLyricsPresenter constructor(
    private val lyricsGateway: OfflineLyricsGateway,
    private val observeUseCase: ObserveOfflineLyricsUseCase,
    private val insertUseCase: InsertOfflineLyricsUseCase

) {

    private var lyricsDisposable: Job? = null
    protected val currentTrackIdPublisher = ConflatedBroadcastChannel<Long>()

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.offer(trackId)
    }

    suspend fun getLyrics(): String {
        return observeLyrics().first().lyrics
    }

    fun observeLyrics(): Flow<LyricsModel> {
        return currentTrackIdPublisher.asFlow().switchMap { id ->
            observeUseCase(id).combineLatest(flowInterval(1, TimeUnit.SECONDS)) { lyrics, _ ->
                LyricsModel(id, lyrics)
            }
        }
    }

    fun transformLyrics(context: Context, bookmark: Int, model: LyricsModel): Spannable {
        val syncAdjustment = lyricsGateway.getSyncAdjustment(model.id).toInt()
        val position = clamp(bookmark + syncAdjustment, 0, Int.MAX_VALUE)
        return transformLyricsInternal(context, position, model.lyrics)
    }

    private fun transformLyricsInternal(
        context: Context,
        bookmark: Int,
        lyrics: String
    ): Spannable {
        // TODO recheck
        val lines = lyrics.split("\n")

        if (searchForSyncedLyrics(lines).take(10).count() == 0) {
            val spannable = SpannableString(lyrics)
            spannable.setSpan(
                ForegroundColorSpan(Color.WHITE),
                0,
                spannable.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable.setSpan(
                AbsoluteSizeSpan(context.dpToPx(16f)),
                0,
                spannable.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            return spannable
        }

        try {
            val notEmptyLines = lines
                .asSequence()
                .map { line ->
                    val index = line.indexOfFirst { it == '[' }
                    if (index == -1) {
                        ""
                    } else {
                        line.substring(index)
                    }
                }
                .filter { it.length > 10 }
                .filter { it[10] != '\r' }
                .filter { it[0] == '[' && it[9] == ']' }
                .filter { it[1].isDigit() && it[2].isDigit() && it[4].isDigit() && it[5].isDigit() }
                .toList()


            val timeList = mutableListOf<Int>()

            for (line in notEmptyLines) {
                val indexOfBracket = line.indexOfFirst { it == '[' }
                if (indexOfBracket == -1) {
                    continue
                }
                val m1 = line[indexOfBracket + 1].toString().toInt() * 10
                val m2 = line[indexOfBracket + 2].toString().toInt()
                val s1 = line[indexOfBracket + 4].toString().toInt() * 10
                val s2 = line[indexOfBracket + 5].toString().toInt()
                val m = TimeUnit.MINUTES.toMillis((m1 + m2).toLong()).toInt()
                val s = TimeUnit.SECONDS.toMillis((s1 + s2).toLong()).toInt()
                timeList.add(m + s)
            }

            val closest = dev.olog.shared.indexOfClosest(bookmark, timeList)

            val result = SpannableStringBuilder()
            for (index in 0..notEmptyLines.lastIndex) {
                val line = notEmptyLines[index].substring(10)
                if (index == closest) {
                    result.append(line)
                    result.setSpan(
                        ForegroundColorSpan(Color.WHITE),
                        result.length - line.length,
                        result.length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    result.setSpan(
                        AbsoluteSizeSpan(context.dpToPx(30f)),
                        result.length - line.length,
                        result.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    result.append(line)
                    result.setSpan(
                        ForegroundColorSpan(0xFF_757575.toInt()),
                        result.length - line.length,
                        result.length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    result.setSpan(
                        AbsoluteSizeSpan(context.dpToPx(16f)),
                        result.length - line.length,
                        result.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                result.appendln()
            }

            return result
        } catch (ex: Throwable) {
            ex.printStackTrace()
            val spannable = SpannableString(lyrics)
            spannable.setSpan(
                ForegroundColorSpan(Color.WHITE),
                0,
                spannable.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable.setSpan(
                AbsoluteSizeSpan(context.dpToPx(16f)),
                0,
                spannable.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            return spannable
        }
    }

    private fun searchForSyncedLyrics(lines: List<String>): Sequence<String> {
        return lines.asSequence()
            .map { it.trim() }
            .map { line ->
                val index = line.indexOfFirst { it == '[' }
                if (index == -1) {
                    ""
                } else {
                    line.substring(index)
                }
            }
            .filter { it.length > 10 }
            .filter { it[10] != '\r' }
            .filter { it[1].isDigit() && it[2].isDigit() && it[4].isDigit() && it[5].isDigit() }
    }

    fun updateSyncAdjustment(value: Long) {
        GlobalScope.launch {
            lyricsGateway.setSyncAdjustment(currentTrackIdPublisher.value, value)
        }
    }

    suspend fun getSyncAdjustment(): String = kotlinx.coroutines.withContext(Dispatchers.IO) {
        "${lyricsGateway.getSyncAdjustment(currentTrackIdPublisher.value)}"
    }

    fun updateLyrics(lyrics: String) {
        if (currentTrackIdPublisher.valueOrNull == null) {
            return
        }
        lyricsDisposable?.cancel()
        lyricsDisposable = GlobalScope.launch {
            insertUseCase(OfflineLyrics(currentTrackIdPublisher.value, lyrics))
        }
    }

}