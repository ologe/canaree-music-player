package dev.olog.msc.app.shortcuts

import android.content.Context
import android.content.Intent
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.getBitmapAsync
import dev.olog.msc.utils.k.extension.toast
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class BaseAppShortcuts(
        protected val context: Context

) : AppShortcuts {

    override fun addDetailShortcut(mediaId: MediaId, title: String, image: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            Completable.create {

                val intent = Intent(context, MainActivity::class.java)
                intent.action = AppConstants.SHORTCUT_DETAIL
                intent.putExtra(AppConstants.SHORTCUT_DETAIL_MEDIA_ID, mediaId.toString())

                val model = DisplayableItem(0, mediaId, "", image = image)
                val bitmap = context.getBitmapAsync(model, 128, { circleCrop() })
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

}