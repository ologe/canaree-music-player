package dev.olog.msc

import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.appshortcuts.ShortcutsActivity
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity
import dev.olog.shared.Classes
import org.junit.Assert
import org.junit.Test

class ClassTest {

    @Test
    fun checkMusicServiceExistence(){
        // activities
        Assert.assertEquals(Classes.MAIN_ACTIIVTY, MainActivity::class.java.name)
        Assert.assertEquals(Classes.SHORTCUTS_ACTIVITY, ShortcutsActivity::class.java.name)
        Assert.assertEquals(Classes.PLAYLIST_CHOOSER_ACTIVITY, PlaylistChooserActivity::class.java.name)
        // services
        Assert.assertEquals(Classes.MUSIC_SERVICE, MusicService::class.java.name)
    }

}