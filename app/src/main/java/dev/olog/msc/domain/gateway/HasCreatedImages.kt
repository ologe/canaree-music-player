package dev.olog.msc.domain.gateway

import io.reactivex.Single

interface HasCreatedImages {

    fun createImages() : Single<Any>

}