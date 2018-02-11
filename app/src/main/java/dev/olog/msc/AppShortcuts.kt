package dev.olog.msc

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ShortcutManager
import android.os.Build
import dev.olog.msc.utils.AppShortcutInfo
import dev.olog.msc.utils.isNougat_MR1

object AppShortcuts {

    @TargetApi(Build.VERSION_CODES.N_MR1)
    fun setup(context: Context){
        if (!isNougat_MR1()){
            return
        }

        val shortcutManager = context.getSystemService(ShortcutManager::class.java)

        val search = AppShortcutInfo.search(context)
        val shuffle = AppShortcutInfo.shuffle(context)
        val play = AppShortcutInfo.play(context)

        shortcutManager.removeAllDynamicShortcuts()
        shortcutManager.addDynamicShortcuts(
                listOf(search, shuffle, play)
        )
    }


}