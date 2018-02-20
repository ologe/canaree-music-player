package dev.olog.msc.presentation.image.creation

import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesThreadPool @Inject constructor() {

    private val threadCount = Runtime.getRuntime().availableProcessors()
    private val threadPoolExecutor = Executors.newFixedThreadPool(threadCount)
    val scheduler = Schedulers.from(threadPoolExecutor)
}