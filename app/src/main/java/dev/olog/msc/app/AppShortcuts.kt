package dev.olog.msc.app

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
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.presentation.shortcuts.ShortcutsActivity
import dev.olog.msc.utils.isNougat_MR1
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppShortcuts @Inject constructor(
        @ApplicationContext private val context: Context

){

    private val shortcutManager = context.systemService<ShortcutManager>()

    init {
        if (isNougat_MR1()){
            shortcutManager.removeAllDynamicShortcuts()
            shortcutManager.addDynamicShortcuts(listOf(
                    search(context),
                    shuffle(context),
                    play(context)
            ))
        }
    }

    fun disablePlay(){
        if (isNougat_MR1()){
            shortcutManager.removeDynamicShortcuts(listOf(MusicConstants.ACTION_PLAY))
        }
    }

    fun enablePlay(){
        if (isNougat_MR1()){
            shortcutManager.addDynamicShortcuts(listOf(play(context)))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun search(context: Context): ShortcutInfo {
        return ShortcutInfo.Builder(context, AppConstants.SHORTCUT_SEARCH)
                .setShortLabel(context.getString(R.string.shortcut_search))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_search))
                .setIntent(createSearchIntent(context))
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun play(context: Context): ShortcutInfo {
        return ShortcutInfo.Builder(context, MusicConstants.ACTION_PLAY)
                .setShortLabel(context.getString(R.string.shortcut_play))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_play))
                .setIntent(createPlayIntent(context))
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun shuffle(context: Context): ShortcutInfo {
        return ShortcutInfo.Builder(context, MusicConstants.ACTION_SHUFFLE)
                .setShortLabel(context.getString(R.string.shortcut_shuffle))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_shuffle))
                .setIntent(createShuffleIntent(context))
                .build()
    }

    private fun createSearchIntent(context: Context): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = AppConstants.SHORTCUT_SEARCH
        return intent
    }

    private fun createPlayIntent(context: Context): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = MusicConstants.ACTION_PLAY
        return intent
    }

    private fun createShuffleIntent(context: Context): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = MusicConstants.ACTION_SHUFFLE
        return intent
    }

}