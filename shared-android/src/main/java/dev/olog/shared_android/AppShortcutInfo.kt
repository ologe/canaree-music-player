package dev.olog.shared_android

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N_MR1)
object AppShortcutInfo {

    const val SHORTCUT_SEARCH = "search"
    const val SHORTCUT_PLAY = "play"
    const val SHORTCUT_SHUFFLE = "shuffle"

    fun search(context: Context, activityClass: Class<*>): ShortcutInfo {
        return ShortcutInfo.Builder(context, SHORTCUT_SEARCH)
                .setShortLabel(context.getString(R.string.shortcut_search))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_search))
                .setIntent(createSearchIntent(context, activityClass))
                .build()
    }

    fun play(context: Context, activityClass: Class<*>): ShortcutInfo {
        return ShortcutInfo.Builder(context, SHORTCUT_PLAY)
                .setShortLabel(context.getString(R.string.shortcut_play))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_play))
                .setIntent(createPlayIntent(context, activityClass))
                .build()
    }

    fun shuffle(context: Context, activityClass: Class<*>): ShortcutInfo{
        return ShortcutInfo.Builder(context, SHORTCUT_SHUFFLE)
                .setShortLabel(context.getString(R.string.shortcut_shuffle))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_shuffle))
                .setIntent(createShuffleIntent(context, activityClass))
                .build()
    }

    private fun createSearchIntent(context: Context, mainActivityClass: Class<*>): Intent {
        val intent = Intent(context, mainActivityClass)
        intent.action = Constants.SHORTCUT_SEARCH
        return intent
    }

    private fun createPlayIntent(context: Context, activityClass: Class<*>): Intent{
        val intent = Intent(context, activityClass)
        intent.action = Constants.SHORTCUT_PLAY
        return intent
    }

    private fun createShuffleIntent(context: Context, activityClass: Class<*>): Intent{
        val intent = Intent(context, activityClass)
        intent.action = Constants.SHORTCUT_SHUFFLE
        return intent
    }

}