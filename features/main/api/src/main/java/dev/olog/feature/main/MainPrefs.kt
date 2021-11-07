package dev.olog.feature.main

import dev.olog.core.Preference

interface MainPrefs {

    val firstAccess: Preference<Boolean>

    val lastBottomNavigationPage: Preference<BottomNavigationPage>

}