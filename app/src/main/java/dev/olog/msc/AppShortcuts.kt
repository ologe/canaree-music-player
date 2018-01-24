package dev.olog.msc

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.activity_shortcuts.ShortcutsActivity
import dev.olog.shared_android.AppShortcutInfo
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

        val search = AppShortcutInfo.search(context, MainActivity::class.java)
        val play = AppShortcutInfo.play(context, ShortcutsActivity::class.java)
        val shuffle = AppShortcutInfo.shuffle(context, ShortcutsActivity::class.java)

        shortcutManager.dynamicShortcuts = listOf(search, play, shuffle)
    }


}