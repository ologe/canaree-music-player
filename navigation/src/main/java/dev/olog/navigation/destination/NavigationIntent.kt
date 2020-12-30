package dev.olog.navigation.destination

import android.content.Intent
import javax.inject.Provider

enum class NavigationIntent {
    PLAYLIST_CHOOSER,
    MAIN_ACTIVITY,
    SEARCH,
    DETAIL,
    FLOATING_SERVICE,
    MUSIC_SERVICE,
    MUSIC_SERVICE_PLAY_FROM_SEARCH,
    MUSIC_SERVICE_PLAY_URI,
    SHORTCUTS_PLAY,
    SHORTCUTS_SHUFFLE,
}

typealias NavigationIntents = Map<NavigationIntent, @JvmSuppressWildcards Provider<Intent>>

val NavigationIntents.mainActivityClass: Class<*>
    get() {
        val className = get(NavigationIntent.MAIN_ACTIVITY)?.get()!!.component!!.className
        return Class.forName(className)
    }

val NavigationIntents.musicServiceClass: Class<*>
    get() {
        val className = get(NavigationIntent.MUSIC_SERVICE)?.get()!!.component!!.className
        return Class.forName(className)
    }