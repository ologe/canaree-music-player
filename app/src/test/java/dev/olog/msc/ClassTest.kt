package dev.olog.msc

import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.playlist.chooser.PlaylistChooserActivity
import dev.olog.intents.Classes
import org.junit.Assert
import org.junit.Test

class ClassTest {

    @Test
    fun checkClassExistence(){
        // activities
        Assert.assertEquals(Classes.ACTIVITY_MAIN, MainActivity::class.java.name)
        Assert.assertEquals(Classes.ACTIVITY_PLAYLIST_CHOOSER, PlaylistChooserActivity::class.java.name)
        // services
    }

}