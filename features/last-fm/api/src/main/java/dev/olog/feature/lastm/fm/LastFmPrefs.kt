package dev.olog.feature.lastm.fm

import dev.olog.core.Preference
import dev.olog.core.Prefs
import dev.olog.core.entity.UserCredentials

interface LastFmPrefs : Prefs {

    val credentials: Preference<UserCredentials>

}