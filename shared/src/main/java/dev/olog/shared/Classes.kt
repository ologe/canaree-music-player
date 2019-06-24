package dev.olog.shared

object Classes {
    const val ACTIVITY_MAIN = "dev.olog.msc.presentation.main.MainActivity"
    const val ACTIVITY_SHORTCUTS = "dev.olog.msc.presentation.shortcuts.ShortcutsActivity"
    const val ACTIVITY_PLAYLIST_CHOOSER = "dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity"

    const val SERVICE_MUSIC = "dev.olog.msc.music.service.MusicService"

    const val WIDGET_COLORED = "dev.olog.msc.presentation.app.widget.defaul.WidgetColored"
    const val WIDGET_COLORED_WITH_QUEUE = "dev.olog.msc.presentation.app.widget.queue.WidgetColoredWithQueue"

    val widgets: List<Class<*>> by lazy {
        listOf(
            Class.forName(WIDGET_COLORED),
            Class.forName(WIDGET_COLORED_WITH_QUEUE)
        )
    }

}