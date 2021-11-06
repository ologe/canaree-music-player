package dev.olog.msc

import dev.olog.appshortcuts.ShortcutsActivity
import dev.olog.service.music.MusicService
import dev.olog.msc.appwidgets.WidgetColored
import dev.olog.presentation.main.MainActivity
import dev.olog.feature.playlist.choose.PlaylistChooserActivity
import dev.olog.intents.Classes
import org.junit.Assert
import org.junit.Test

class ClassTest {

    @Test
    fun checkClassExistence(){
        // activities
        Assert.assertEquals(Classes.ACTIVITY_MAIN, MainActivity::class.java.name)
        Assert.assertEquals(Classes.ACTIVITY_SHORTCUTS, ShortcutsActivity::class.java.name)
        Assert.assertEquals(Classes.ACTIVITY_PLAYLIST_CHOOSER, dev.olog.feature.playlist.choose.PlaylistChooserActivity::class.java.name)
        // services
        Assert.assertEquals(Classes.SERVICE_MUSIC, MusicService::class.java.name)
        //widgets
        Assert.assertEquals(Classes.WIDGET_COLORED, WidgetColored::class.java.name)
    }

}