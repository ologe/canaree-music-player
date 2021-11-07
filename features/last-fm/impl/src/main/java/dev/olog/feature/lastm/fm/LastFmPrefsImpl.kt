package dev.olog.feature.lastm.fm

import dev.olog.core.Preference
import dev.olog.core.PreferenceManager
import dev.olog.core.entity.UserCredentials
import javax.inject.Inject

class LastFmPrefsImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
) : LastFmPrefs {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val LAST_FM_USERNAME = "$TAG.LAST_FM_USERNAME_2"
        private const val LAST_FM_PASSWORD = "$TAG.LAST_FM_PASSWORD_2"
    }

    override val credentials: Preference<UserCredentials>
        get() = preferenceManager.createComposed(
            keyDefault1 = LAST_FM_USERNAME to "",
            keyDefault2 = LAST_FM_PASSWORD to "",
            serialize = { it.username to it.password },
            deserialize = { username, password ->
                UserCredentials(username, password)
            }
        )
}