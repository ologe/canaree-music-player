package dev.olog.intents

object Classes {
    const val ACTIVITY_MAIN = "dev.olog.msc.main.MainActivity"
    const val ACTIVITY_SHORTCUTS = "dev.olog.appshortcuts.ShortcutsActivity"
    const val ACTIVITY_PLAYLIST_CHOOSER = "dev.olog.feature.playlist.choose.PlaylistChooserActivity"

    const val SERVICE_MUSIC = "dev.olog.service.music.MusicService"

    const val WIDGET_COLORED = "dev.olog.msc.appwidgets.WidgetColored"

    @JvmStatic
    val widgets: List<Class<*>> by lazy {
        listOf(
            Class.forName(WIDGET_COLORED)
        )
    }

}