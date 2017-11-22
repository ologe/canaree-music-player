package dev.olog.domain.gateway.prefs

interface AppPreferencesGateway {

    fun isFirstAccess(): Boolean

}