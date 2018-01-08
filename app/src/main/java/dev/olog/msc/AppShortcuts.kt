package dev.olog.msc

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.shared_android.Constants
import dev.olog.shared_android.isNougat_MR1

object AppShortcuts {

    @TargetApi(Build.VERSION_CODES.N_MR1)
    fun setup(context: Context){
        if (!isNougat_MR1()){
            return
        }

        val shortcutManager = context.getSystemService(ShortcutManager::class.java)

        if (shortcutManager.dynamicShortcuts.isNotEmpty()) {
            // shortcuts already created
            return
        }

        val search = ShortcutInfo.Builder(context, "search")
                .setShortLabel(context.getString(R.string.shortcut_search))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_search))
                .setIntent(createSearchIntent(context))
                .build()

        val play = ShortcutInfo.Builder(context, "play")
                .setShortLabel(context.getString(R.string.shortcut_play))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_play))
                .setIntent(createPlayIntent(context))
                .build()

        val shuffle = ShortcutInfo.Builder(context, "shuffle")
                .setShortLabel(context.getString(R.string.shortcut_shuffle))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_shuffle))
                .setIntent(createShuffleIntent(context))
                .build()

        shortcutManager.dynamicShortcuts = listOf(search, play, shuffle)
    }

    private fun createSearchIntent(context: Context): Intent{
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Constants.SHORTCUT_SEARCH
        return intent
    }

    private fun createPlayIntent(context: Context): Intent{
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Constants.SHORTCUT_PLAY
        return intent
    }

    private fun createShuffleIntent(context: Context): Intent{
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Constants.SHORTCUT_SHUFFLE
        return intent
    }

}