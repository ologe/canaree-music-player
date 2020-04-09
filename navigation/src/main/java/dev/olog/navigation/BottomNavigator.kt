package dev.olog.navigation

import androidx.fragment.app.FragmentActivity
import dev.olog.navigation.screens.FragmentScreen

interface BottomNavigator {

    fun bottomNavigate(
        activity: FragmentActivity,
        screen: FragmentScreen
    )

}