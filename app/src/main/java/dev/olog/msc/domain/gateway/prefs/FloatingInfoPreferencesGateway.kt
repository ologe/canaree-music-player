package dev.olog.msc.domain.gateway.prefs

import io.reactivex.Flowable

interface FloatingInfoPreferencesGateway {

    fun getInfoRequest(): Flowable<String>

    fun setInfoRequest(newInfoRequest: String)

}