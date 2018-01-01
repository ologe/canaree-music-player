package dev.olog.domain.gateway

import io.reactivex.Single

interface HasCreatedImages {

    fun createImages() : Single<Any>

}