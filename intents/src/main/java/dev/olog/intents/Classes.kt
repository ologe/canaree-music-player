package dev.olog.intents

object Classes {
    const val ACTIVITY_MAIN = "dev.olog.presentation.main.MainActivity"
    const val ACTIVITY_SHORTCUTS = "dev.olog.appshortcuts.ShortcutsActivity"
    const val ACTIVITY_PLAYLIST_CHOOSER = "dev.olog.presentation.playlist.chooser.PlaylistChooserActivity"

    const val WIDGET_COLORED = "dev.olog.msc.appwidgets.WidgetColored"

    @JvmStatic
    val widgets: List<Class<*>> by lazy {
        listOf(
            Class.forName(WIDGET_COLORED)
        )
    }

}