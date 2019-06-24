package dev.olog.appshortcuts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dev.olog.core.MediaId
import dev.olog.image.provider.getCachedBitmap
import dev.olog.shared.Classes
import dev.olog.shared.CustomScope
import dev.olog.shared.extensions.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseAppShortcuts(
    protected val context: Context
) : CoroutineScope by CustomScope() {

    fun addDetailShortcut(mediaId: MediaId, title: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            launch {
                val intent = Intent(context, Class.forName(Classes.MAIN_ACTIIVTY))
                intent.action = Shortcuts.DETAIL
                intent.putExtra(Shortcuts.DETAIL_EXTRA_ID, mediaId.toString())

                val bitmap = context.getCachedBitmap(mediaId, 128, { circleCrop() })
                val shortcut = ShortcutInfoCompat.Builder(context, title)
                    .setShortLabel(title)
                    .setIcon(IconCompat.createWithBitmap(bitmap))
                    .setIntent(intent)
                    .build()

                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
                withContext(Dispatchers.Main) {
                    onAddedSuccess(context)
                }
            }


        } else {
            onAddedNotSupported(context)
        }
    }

    private fun onAddedSuccess(context: Context) {
        context.toast(R.string.app_shortcut_added_to_home_screen)
    }

    private fun onAddedNotSupported(context: Context) {
        context.toast(R.string.app_shortcut_add_to_home_screen_not_supported)
    }

    abstract fun disablePlay()
    abstract fun enablePlay()

}