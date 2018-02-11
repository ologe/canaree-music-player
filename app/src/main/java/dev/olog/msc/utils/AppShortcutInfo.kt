package dev.olog.msc.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.RequiresApi
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.presentation.shortcuts.ShortcutsActivity

@RequiresApi(Build.VERSION_CODES.N_MR1)
object AppShortcutInfo {

    const val SHORTCUT_SEARCH = "search"
    const val SHORTCUT_PLAY = "play"
    const val SHORTCUT_SHUFFLE = "shuffle"

    fun search(context: Context): ShortcutInfo {
        return ShortcutInfo.Builder(context, SHORTCUT_SEARCH)
                .setShortLabel(context.getString(R.string.shortcut_search))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_search))
                .setIntent(createSearchIntent(context))
                .build()
    }

    fun play(context: Context): ShortcutInfo {
        return ShortcutInfo.Builder(context, SHORTCUT_PLAY)
                .setShortLabel(context.getString(R.string.shortcut_play))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_play))
                .setIntent(createPlayIntent(context))
                .build()
    }

    fun shuffle(context: Context): ShortcutInfo{
        return ShortcutInfo.Builder(context, SHORTCUT_SHUFFLE)
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

    private fun createPlayIntent(context: Context): Intent{
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = MusicConstants.ACTION_PLAY
        return intent
    }

    private fun createShuffleIntent(context: Context): Intent{
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = MusicConstants.ACTION_SHUFFLE
        return intent
    }

}