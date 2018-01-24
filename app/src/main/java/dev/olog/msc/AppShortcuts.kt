package dev.olog.msc

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ShortcutManager
import android.os.Build
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.activity_shortcuts.ShortcutsActivity
import dev.olog.shared_android.AppShortcutInfo
import dev.olog.shared_android.isNougat_MR1

object AppShortcuts {

    @TargetApi(Build.VERSION_CODES.N_MR1)
    fun setup(context: Context){
        if (!isNougat_MR1()){
            return
        }

        val shortcutManager = context.getSystemService(ShortcutManager::class.java)

        val search = AppShortcutInfo.search(context, MainActivity::class.java)
        val shuffle = AppShortcutInfo.shuffle(context, ShortcutsActivity::class.java)
        val play = AppShortcutInfo.play(context, ShortcutsActivity::class.java)

        shortcutManager.removeAllDynamicShortcuts()
        shortcutManager.addDynamicShortcuts(
                listOf(search, shuffle, play)
        )
    }


}