package dev.olog.msc.app.shortcuts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dev.olog.core.MediaId
import dev.olog.injection.shortcuts.AppShortcuts
import dev.olog.msc.R
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.utils.k.extension.getCachedBitmap
import dev.olog.shared.toast

abstract class BaseAppShortcuts(
        protected val context: Context
) : AppShortcuts {

    override fun addDetailShortcut(mediaId: MediaId, title: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            val intent = Intent(context, MainActivity::class.java)
            intent.action = Shortcuts.DETAIL
            intent.putExtra(Shortcuts.DETAIL_EXTRA_ID, mediaId.toString())

            val bitmap = context.getCachedBitmap(mediaId, 128, { circleCrop() })
            val shortcut = ShortcutInfoCompat.Builder(context, title)
                .setShortLabel(title)
                .setIcon(IconCompat.createWithBitmap(bitmap))
                .setIntent(intent)
                .build()

            ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
            onAddedSuccess(context)

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