package dev.olog.navigation

import android.net.Uri

interface ServiceNavigator {

    fun toFloatingWindow()

    fun toMusicPlayFromUri(uri: Uri?)
    fun toMusicPlayFromSearch()

}