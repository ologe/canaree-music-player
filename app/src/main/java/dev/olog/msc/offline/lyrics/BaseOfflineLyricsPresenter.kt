package dev.olog.msc.offline.lyrics

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.offline.lyrics.InsertOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.offline.lyrics.ObserveOfflineLyricsUseCase
import dev.olog.shared.utils.clamp
import dev.olog.shared.extensions.dpToPx
import dev.olog.shared.utils.indexOfClosest
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

abstract class BaseOfflineLyricsPresenter constructor(
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val observeUseCase: ObserveOfflineLyricsUseCase,
    private val insertUseCase: InsertOfflineLyricsUseCase

) {

    private var lyricsDisposable: Disposable? = null
    protected val currentTrackIdPublisher = BehaviorSubject.create<Long>()

    private var originalLyrics: String = ""

    fun updateCurrentTrackId(trackId: Long){
        currentTrackIdPublisher.onNext(trackId)
    }

    fun observeLyrics(): Observable<String> {
        return Observables.combineLatest(
                currentTrackIdPublisher.switchMap { id ->
                    observeUseCase.execute(id)
                }, Observable.interval(1, TimeUnit.SECONDS, Schedulers.io()).startWith(0)
                ) { lyrics, _ ->
                    this.originalLyrics = lyrics
                    lyrics
                }
    }

    fun getOriginalLyrics() = originalLyrics

    fun transformLyrics(context: Context, bookmark: Int, lyrics: String): Spannable {
        val syncAdjustment = appPreferencesUseCase.getSyncAdjustment().toInt()
        val position = clamp(bookmark + syncAdjustment, 0, Int.MAX_VALUE)
        return transformLyricsInternal(context, position, lyrics)
    }

    private fun transformLyricsInternal(context: Context, bookmark: Int, lyrics: String): Spannable {
        val lines = lyrics.split("\n")

        if (searchForSyncedLyrics(lines).take(10).count() == 0){
            val spannable = SpannableString(lyrics)
            spannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, spannable.length,  Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.setSpan(AbsoluteSizeSpan(context.dpToPx(16f)), 0, spannable.length,  Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            return spannable
        }

        try {
            val notEmptyLines = lines
                    .asSequence()
                    .map { line ->
                        val index = line.indexOfFirst { it == '[' }
                        if (index == -1){
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
                if (indexOfBracket == -1){
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

            val closest = indexOfClosest(bookmark, timeList)

            val result = SpannableStringBuilder()
            for (index in 0..notEmptyLines.lastIndex){
                val line = notEmptyLines[index].substring(10)
                if (index == closest){
                    result.append(line)
                    result.setSpan(ForegroundColorSpan(Color.WHITE), result.length - line.length, result.length,  Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    result.setSpan(AbsoluteSizeSpan(context.dpToPx(30f)), result.length - line.length, result.length,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    result.append(line)
                    result.setSpan(ForegroundColorSpan(0xFF_757575.toInt()), result.length - line.length, result.length,  Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    result.setSpan(AbsoluteSizeSpan(context.dpToPx(16f)), result.length - line.length, result.length,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                result.appendln()
            }

            return result
        } catch (ex: Exception){
            val spannable = SpannableString(lyrics)
            spannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, spannable.length,  Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.setSpan(AbsoluteSizeSpan(context.dpToPx(16f)), 0, spannable.length,  Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            return spannable
        }
    }

    private fun searchForSyncedLyrics(lines: List<String>): Sequence<String> {
        return lines.asSequence()
                .map { line ->
                    val index = line.indexOfFirst { it == '[' }
                    if (index == -1){
                        ""
                    } else {
                        line.substring(index)
                    }
                }
                .filter { it.length > 10 }
                .filter { it[10] != '\r' }
                .filter { it[1].isDigit() && it[2].isDigit() && it[4].isDigit() && it[5].isDigit() }
    }

    fun updateSyncAdjustement(value: Long){
        appPreferencesUseCase.setSyncAdjustment(value)
    }

    fun getSyncAdjustement(): String {
        return "${appPreferencesUseCase.getSyncAdjustment()}"
    }

    fun updateLyrics(lyrics: String){
        lyricsDisposable.unsubscribe()
        lyricsDisposable = insertUseCase.execute(
            OfflineLyrics(
                currentTrackIdPublisher.value ?: -1,
                lyrics
            )
        )
                .subscribe({}, Throwable::printStackTrace)
    }

}