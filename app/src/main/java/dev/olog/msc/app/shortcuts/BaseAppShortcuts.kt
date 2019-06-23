package dev.olog.msc.app.shortcuts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.MediaId
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.utils.k.extension.getCachedBitmap
import dev.olog.msc.utils.k.extension.toast
import dev.olog.shared.unsubscribe
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseAppShortcuts(
        protected val context: Context,
        @ProcessLifecycle lifecycle: Lifecycle

) : AppShortcuts, DefaultLifecycleObserver {

    private var disposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun addDetailShortcut(mediaId: MediaId, title: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            disposable.unsubscribe()
            disposable = Completable.create {

                val intent = Intent(context, MainActivity::class.java)
                intent.action = AppConstants.SHORTCUT_DETAIL
                intent.putExtra(AppConstants.SHORTCUT_DETAIL_MEDIA_ID, mediaId.toString())

                val bitmap = context.getCachedBitmap(mediaId, 128, { circleCrop() })
                val shortcut = ShortcutInfoCompat.Builder(context, title)
                        .setShortLabel(title)
                        .setIcon(IconCompat.createWithBitmap(bitmap))
                        .setIntent(intent)
                        .build()

                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)

                it.onComplete()

            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ onAddedSuccess(context) }, Throwable::printStackTrace)

        } else {
            onAddedNotSupported(context)
        }
    }

    private fun onAddedSuccess(context: Context){
        context.toast(R.string.app_shortcut_added_to_home_screen)
    }

    private fun onAddedNotSupported(context: Context){
        context.toast(R.string.app_shortcut_add_to_home_screen_not_supported)
    }

    override fun onStop(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

}