package dev.olog.msc

import dev.olog.feature.shortcuts.ShortcutsActivity
import dev.olog.intents.Classes
import dev.olog.msc.appwidgets.WidgetColored
import dev.olog.presentation.main.MainActivity
import org.junit.Assert
import org.junit.Test

class ClassTest {

    @Test
    fun checkClassExistence(){
        // activities
        Assert.assertEquals(Classes.ACTIVITY_MAIN, MainActivity::class.java.name)
        //widgets
        Assert.assertEquals(Classes.WIDGET_COLORED, WidgetColored::class.java.name)
    }

}