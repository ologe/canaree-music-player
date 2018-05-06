package dev.olog.msc.presentation.offline.lyrics

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.OfflineLyrics
import dev.olog.msc.domain.interactor.offline.lyrics.InsertOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.offline.lyrics.ObserveOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.domain.interactor.prefs.TutorialPreferenceUseCase
import dev.olog.msc.utils.k.extension.clamp
import dev.olog.msc.utils.k.extension.dpToPx
import dev.olog.msc.utils.k.extension.indexOfClosest
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OfflineLyricsFragmentPresenter @Inject constructor(
        private val observeUseCase: ObserveOfflineLyricsUseCase,
        private val insertUseCase: InsertOfflineLyricsUseCase,
        private val tutorialPreferenceUseCase: TutorialPreferenceUseCase,
        private val appPreferencesUseCase: AppPreferencesUseCase
) {

    private var lyricsDisposable: Disposable? = null

    private val currentTrackIdPublisher = BehaviorSubject.create<Long>()

    private var currentTitle: String = ""
    private var currentArtist: String = ""

    fun updateCurrentTrackId(trackId: Long){
        currentTrackIdPublisher.onNext(trackId)
    }

    fun updateCurrentMetadata(title: String, artist: String){
        this.currentTitle = title
        this.currentArtist = artist
    }

    fun observeLyrics(): Observable<String> {
        return Observables.combineLatest(
                currentTrackIdPublisher.switchMap { id ->
                    observeUseCase.execute(id)
                },Observable.interval(1, TimeUnit.SECONDS, Schedulers.io()),
                { lyrics, _ -> lyrics }
        )
    }

    fun updateLyrics(lyrics: String){
        lyricsDisposable.unsubscribe()
        lyricsDisposable = insertUseCase.execute(OfflineLyrics(currentTrackIdPublisher.value ?: -1, lyrics))
                .subscribe({}, Throwable::printStackTrace)
    }

    fun transformLyrics(context: Context, bookmark: Int, lyrics: String): Spannable {
        val syncAdjustment = appPreferencesUseCase.getSyncAdjustment().toInt()
        val position = clamp(bookmark + syncAdjustment, 0, Int.MAX_VALUE)
        return transformLyricsInternal(context, position, lyrics)
    }

    private fun transformLyricsInternal(context: Context, bookmark: Int, lyrics: String): Spannable {
        val lines = lyrics.split("\n")

        lines.take(10) // check only the first 10 lines
                .firstOrNull { it.length > 10 && it[0] == '[' && it[9] == ']' } ?: return SpannableString(lyrics).apply {
            this.setSpan(ForegroundColorSpan(Color.WHITE), 0, this.length,  Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            this.setSpan(AbsoluteSizeSpan(context.dpToPx(16f)), 0, this.length,  Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }

        try {
            val notEmptyLines = lines.filter { it.length > 10 && it[10] != '\r' }

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

    fun getInfoMetadata(): String {
        var result = currentTitle
        if (currentArtist != AppConstants.UNKNOWN_ARTIST){
            result += " $currentArtist"
        }
        result += " lyrics"
        return result
    }

    fun onStop(){
        lyricsDisposable.unsubscribe()
    }

    fun showAddLyricsIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.addLyricsTutorial()
    }

    fun updateSyncAdjustement(value: Long){
        appPreferencesUseCase.setSyncAdjustment(value)
    }

    fun getSyncAdjustement(): String {
        return "${appPreferencesUseCase.getSyncAdjustment()}"
    }

}