package dev.olog.msc

import dev.olog.intents.Classes
import dev.olog.msc.appwidgets.WidgetColored
import org.junit.Assert
import org.junit.Test

class ClassTest {

    @Test
    fun checkClassExistence(){
        //widgets
        Assert.assertEquals(Classes.WIDGET_COLORED, WidgetColored::class.java.name)
    }

}