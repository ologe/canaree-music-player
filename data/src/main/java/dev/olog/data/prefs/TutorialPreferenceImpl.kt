package dev.olog.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.core.prefs.TutorialPreferenceGateway
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TutorialPreferenceImpl @Inject constructor(
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences

) : TutorialPreferenceGateway {

    companion object {
        private const val TAG = "TutorialPreferenceImpl"
        private const val SORT_BY_SHOWN = "$TAG.SORT_BY_SHOWN"
        private const val FLOATING_WINDOW_SHOWN = "$TAG.FLOATING_WINDOW_SHOWN"
        private const val LYRICS_SHOWN = "$TAG.LYRICS_SHOWN"
        private const val ADD_LYRICS_SHOWN = "$TAG.ADD_LYRICS_SHOWN_2"
    }

    override fun sortByTutorial(): Completable {
        return rxPreferences.getBoolean(SORT_BY_SHOWN, false)
                .asObservable()
                .firstOrError()
                .flatMapCompletable {
                    if (it) Completable.error(Throwable("already shown"))
                    else Completable.complete()
                            .doOnComplete { disableSortByTutorial() }
                }.subscribeOn(Schedulers.io())
    }

    override fun floatingWindowTutorial(): Completable {
        return rxPreferences.getBoolean(FLOATING_WINDOW_SHOWN, false)
                .asObservable()
                .firstOrError()
                .flatMapCompletable {
                    if (it) Completable.error(Throwable("already shown"))
                    else Completable.complete()
                            .doOnComplete { disableFloatingWindowTutorial() }
                }.subscribeOn(Schedulers.io())
    }

    override fun lyricsTutorial(): Completable {
        return rxPreferences.getBoolean(LYRICS_SHOWN, false)
                .asObservable()
                .firstOrError()
                .flatMapCompletable {
                    if (it) Completable.error(Throwable("already shown"))
                    else Completable.complete()
                            .doOnComplete { disableLyricsTutorial() }
                }.subscribeOn(Schedulers.io())
    }

    override fun editLyrics(): Completable {
        return rxPreferences.getBoolean(ADD_LYRICS_SHOWN, false)
                .asObservable()
                .firstOrError()
                .flatMapCompletable {
                    if (it) Completable.error(Throwable("already shown"))
                    else Completable.complete()
                            .doOnComplete { disableAddLyricsTutorial() }
                }.subscribeOn(Schedulers.io())
    }

    private fun disableSortByTutorial(){
        preferences.edit { putBoolean(SORT_BY_SHOWN, true) }
    }

    private fun disableFloatingWindowTutorial(){
        preferences.edit { putBoolean(FLOATING_WINDOW_SHOWN, true) }
    }

    private fun disableLyricsTutorial(){
        preferences.edit { putBoolean(LYRICS_SHOWN, true) }
    }

    private fun disableAddLyricsTutorial(){
        preferences.edit { putBoolean(ADD_LYRICS_SHOWN, true) }
    }

    override fun reset() {
        preferences.edit {
            putBoolean(SORT_BY_SHOWN, false)
            putBoolean(FLOATING_WINDOW_SHOWN, false)
            putBoolean(LYRICS_SHOWN, false)
            putBoolean(ADD_LYRICS_SHOWN, false)
        }
    }

}