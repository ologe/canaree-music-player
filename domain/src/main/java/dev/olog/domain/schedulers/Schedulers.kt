package dev.olog.domain.schedulers

import kotlinx.coroutines.CoroutineDispatcher

interface Schedulers {

    val io: CoroutineDispatcher
    val cpu: CoroutineDispatcher
    val main: CoroutineDispatcher

}