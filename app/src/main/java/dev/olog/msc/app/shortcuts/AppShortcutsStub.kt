package dev.olog.msc.app.shortcuts

import android.content.Context
import android.content.Intent
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.utils.MediaId

class AppShortcutsStub(
        private val context: Context

) : AppShortcuts {

    override fun disablePlay() {
    }

    override fun enablePlay() {
    }

    override fun addDetailShortcut(mediaId: MediaId, title: String, image: String) {
        val shortcutIntent = Intent(context, MainActivity::class.java)
        shortcutIntent.action = AppConstants.SHORTCUT_DETAIL
        shortcutIntent.putExtra(AppConstants.SHORTCUT_DETAIL_MEDIA_ID, mediaId.toString())

        val intent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title)
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, getBitmap(context, mediaId, image))
        context.sendBroadcast(intent)

        onAddedSuccess(context)
    }
}