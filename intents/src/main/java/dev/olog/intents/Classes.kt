package dev.olog.intents

@Deprecated("")
object Classes {
    const val WIDGET_COLORED = "dev.olog.msc.appwidgets.WidgetColored"

    val widgets: List<Class<*>> by lazy {
        listOf(
            Class.forName(WIDGET_COLORED)
        )
    }

}