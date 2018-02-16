package dev.olog.msc.app.shortcuts

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.RequiresApi
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.utils.MediaId

@RequiresApi(Build.VERSION_CODES.O)
class AppShortcutsImpl26(
        context: Context

) : AppShortcutsImpl25(context) {

    override fun addDetailShortcut(mediaId: MediaId, title: String, image: String) {
        if (shortcutManager.isRequestPinShortcutSupported) {

            val intent = Intent(context, MainActivity::class.java)
            intent.action = AppConstants.SHORTCUT_DETAIL
            intent.putExtra(AppConstants.SHORTCUT_DETAIL_MEDIA_ID, mediaId.toString())



            val shortcut = ShortcutInfo.Builder(context, title)
                    .setShortLabel(title)
                    .setIcon(Icon.createWithBitmap(getBitmap(context, mediaId, image)))
                    .setIntent(intent)
                    .build()
            shortcutManager.requestPinShortcut(shortcut, null)
            // todo notify shortcut added
        } else {
            // todo notify pin not added
        }
    }

}