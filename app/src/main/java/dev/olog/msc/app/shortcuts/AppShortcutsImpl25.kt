package dev.olog.msc.app.shortcuts

import android.Manifest
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import androidx.core.content.systemService
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.shortcuts.ShortcutsActivity
import dev.olog.msc.utils.isNougat_MR1
import dev.olog.msc.utils.k.extension.toast

@RequiresApi(Build.VERSION_CODES.N_MR1)
class AppShortcutsImpl25(
        context: Context,
        @ProcessLifecycle lifecycle: Lifecycle

) : BaseAppShortcuts(context, lifecycle) {

    private val shortcutManager : ShortcutManager = context.systemService<ShortcutManager>()

    init {
        shortcutManager.removeAllDynamicShortcuts()

        val shortcuts = mutableListOf(search())
        shuffle()?.let { shortcuts.add(it) }
        play()?.let { shortcuts.add(it) }
        shortcutManager.addDynamicShortcuts(shortcuts)
    }

    override fun disablePlay(){
        if (isNougat_MR1()){
            shortcutManager.removeDynamicShortcuts(listOf(MusicConstants.ACTION_PLAY))
        }
    }

    override fun enablePlay(){
        if (isNougat_MR1()){
            play()?.let { shortcutManager.addDynamicShortcuts(listOf(it)) }
        }
    }

    private fun search(): ShortcutInfo {
        return ShortcutInfo.Builder(context, AppConstants.SHORTCUT_SEARCH)
                .setShortLabel(context.getString(R.string.shortcut_search))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_search))
                .setIntent(createSearchIntent())
                .build()
    }

    private fun play(): ShortcutInfo? {
        if (!checkStoragePermission(context)){
            context.toast(R.string.shortcut_missing_storage_permission)
            return null
        }

        return ShortcutInfo.Builder(context, MusicConstants.ACTION_PLAY)
                .setShortLabel(context.getString(R.string.shortcut_play))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_play))
                .setIntent(createPlayIntent())
                .build()
    }

    private fun shuffle(): ShortcutInfo? {
        if (!checkStoragePermission(context)){
            context.toast(R.string.shortcut_missing_storage_permission)
            return null
        }

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

    private fun checkStoragePermission(context: Context): Boolean{
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

}