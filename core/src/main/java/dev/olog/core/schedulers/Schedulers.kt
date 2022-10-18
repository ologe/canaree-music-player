package dev.olog.core.schedulers

import kotlinx.coroutines.CoroutineDispatcher

data class Schedulers(
    val io: CoroutineDispatcher,
    val cpu: CoroutineDispatcher,
    val main: CoroutineDispatcher,
)