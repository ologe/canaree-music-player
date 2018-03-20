package dev.olog.msc.presentation.image.creation

import android.support.v4.math.MathUtils
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesThreadPool @Inject constructor() {

    private val threads = Runtime.getRuntime().availableProcessors()
    private val threadPoolExecutor = Executors.newFixedThreadPool(MathUtils.clamp(threads / 2, 1, 2))
    val scheduler = Schedulers.from(threadPoolExecutor)
}