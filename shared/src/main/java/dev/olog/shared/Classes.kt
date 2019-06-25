package dev.olog.shared

object Classes {
    const val ACTIVITY_MAIN = "dev.olog.msc.presentation.main.MainActivity"
    const val ACTIVITY_SHORTCUTS = "dev.olog.appshortcuts.ShortcutsActivity"
    const val ACTIVITY_PLAYLIST_CHOOSER = "dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity"

    const val SERVICE_MUSIC = "dev.olog.msc.music.service.MusicService"
    const val SERVICE_FLOATING = "dev.olog.msc.floating.window.service.FloatingWindowService"

    const val WIDGET_COLORED = "dev.olog.msc.presentation.app.widget.WidgetColored"

    val widgets: List<Class<*>> by lazy {
        listOf(
            Class.forName(WIDGET_COLORED)
        )
    }

}