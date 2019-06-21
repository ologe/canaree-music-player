package dev.olog.msc.presentation.image.creation

import dev.olog.shared.clamp
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesThreadPool @Inject constructor() {

    private val threads = Runtime.getRuntime().availableProcessors()
    private val threadPoolExecutor = Executors.newFixedThreadPool(clamp(threads / 2, 1, 2))
    val scheduler = Schedulers.from(threadPoolExecutor)
}