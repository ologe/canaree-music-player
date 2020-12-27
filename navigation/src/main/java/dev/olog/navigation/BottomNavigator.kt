package dev.olog.navigation

import dev.olog.navigation.destination.FragmentScreen

interface BottomNavigator {

    companion object {
        val TAGS = listOf(
            FragmentScreen.LIBRARY_TRACKS.tag,
            FragmentScreen.LIBRARY_PODCASTS.tag,
            FragmentScreen.SEARCH.tag,
            FragmentScreen.QUEUE.tag,
        )
    }

    fun bottomNavigate(page: BottomNavigationPage)

}