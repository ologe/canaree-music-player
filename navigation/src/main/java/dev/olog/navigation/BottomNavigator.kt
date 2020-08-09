package dev.olog.navigation

import dev.olog.navigation.screens.FragmentScreen

interface BottomNavigator {

    companion object {
        val TAGS = listOf(
            FragmentScreen.HOME.tag,
            FragmentScreen.LIBRARY.tag,
            FragmentScreen.SEARCH.tag,
            FragmentScreen.PLAYLISTS.tag,
            FragmentScreen.QUEUE.tag
        )
    }

    fun bottomNavigate(screen: FragmentScreen)
    fun toLibraryChooser()

}