package dev.olog.intents

object Classes {

    const val WIDGET_COLORED = "dev.olog.msc.appwidgets.WidgetColored"

    @JvmStatic
    val widgets: List<Class<*>> by lazy {
        listOf(
            Class.forName(WIDGET_COLORED)
        )
    }

}