package dev.olog.msc

import dev.olog.appshortcuts.ShortcutsActivity
import dev.olog.service.floating.FloatingWindowService
import dev.olog.service.music.MusicService
import dev.olog.msc.presentation.app.widget.WidgetColored
import dev.olog.msc.presentation.main.di.MainActivity
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity
import dev.olog.shared.Classes
import org.junit.Assert
import org.junit.Test

class ClassTest {

    @Test
    fun checkMusicServiceExistence(){
        // activities
        Assert.assertEquals(Classes.ACTIVITY_MAIN, MainActivity::class.java.name)
        Assert.assertEquals(Classes.ACTIVITY_SHORTCUTS, ShortcutsActivity::class.java.name)
        Assert.assertEquals(Classes.ACTIVITY_PLAYLIST_CHOOSER, PlaylistChooserActivity::class.java.name)
        // services
        Assert.assertEquals(Classes.SERVICE_MUSIC, MusicService::class.java.name)
        Assert.assertEquals(Classes.SERVICE_FLOATING, FloatingWindowService::class.java.name)
        //widgets
        Assert.assertEquals(Classes.WIDGET_COLORED, WidgetColored::class.java.name)
    }

}