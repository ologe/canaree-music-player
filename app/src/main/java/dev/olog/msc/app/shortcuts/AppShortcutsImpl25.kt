package dev.olog.msc.app.shortcuts

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.RequiresApi
import androidx.content.systemService
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.shortcuts.ShortcutsActivity
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.isNougat_MR1

@RequiresApi(Build.VERSION_CODES.N_MR1)
open class AppShortcutsImpl25(
        protected val context: Context

) : AppShortcuts {

    protected val shortcutManager : ShortcutManager = context.systemService<ShortcutManager>()

    init {
        shortcutManager.removeAllDynamicShortcuts()
        shortcutManager.addDynamicShortcuts(listOf(
                search(), shuffle(), play()))
    }

    override fun disablePlay(){
        if (isNougat_MR1()){
            shortcutManager.removeDynamicShortcuts(listOf(MusicConstants.ACTION_PLAY))
        }
    }

    override fun enablePlay(){
        if (isNougat_MR1()){
            shortcutManager.addDynamicShortcuts(listOf(play()))
        }
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

    private fun search(): ShortcutInfo {
        return ShortcutInfo.Builder(context, AppConstants.SHORTCUT_SEARCH)
                .setShortLabel(context.getString(R.string.shortcut_search))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_search))
                .setIntent(createSearchIntent())
                .build()
    }

    private fun play(): ShortcutInfo {
        return ShortcutInfo.Builder(context, MusicConstants.ACTION_PLAY)
                .setShortLabel(context.getString(R.string.shortcut_play))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_play))
                .setIntent(createPlayIntent())
                .build()
    }

    private fun shuffle(): ShortcutInfo {
        return ShortcutInfo.Builder(context, MusicConstants.ACTION_SHUFFLE)
                .setShortLabel(context.getString(R.string.shortcut_shuffle))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_shuffle))
                .setIntent(createShuffleIntent())
                .build()
    }

    private fun createSearchIntent(): Intent {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = AppConstants.SHORTCUT_SEARCH
        return intent
    }

    private fun createPlayIntent(): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = MusicConstants.ACTION_PLAY
        return intent
    }

    private fun createShuffleIntent(): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = MusicConstants.ACTION_SHUFFLE
        return intent
    }

}