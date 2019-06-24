package dev.olog.msc

import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.appshortcuts.ShortcutsActivity
import dev.olog.msc.presentation.app.widget.defaul.WidgetColored
import dev.olog.msc.presentation.app.widget.queue.WidgetColoredWithQueue
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
        //widgets
        Assert.assertEquals(Classes.WIDGET_COLORED, WidgetColored::class.java.name)
        Assert.assertEquals(Classes.WIDGET_COLORED_WITH_QUEUE, WidgetColoredWithQueue::class.java.name)
    }

}