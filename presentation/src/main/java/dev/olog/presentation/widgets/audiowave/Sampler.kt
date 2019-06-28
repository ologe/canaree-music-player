package dev.olog.presentation.widgets.audiowave

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal val MAIN_THREAD = Handler(Looper.getMainLooper())
internal val SAMPLER_THREAD: ExecutorService = Executors.newSingleThreadExecutor()

object Sampler {

    fun downSampleAsync(data: ByteArray, targetSize: Int, answer: (ByteArray) -> Unit) {
        SAMPLER_THREAD.submit {
            val scaled =
                downSample(data, targetSize)

            MAIN_THREAD.post {
                answer(scaled)
            }
        }
    }

    fun downSample(data: ByteArray, targetSize: Int): ByteArray {
        val targetSized = ByteArray(targetSize)
        val chunkSize = data.size / targetSize
        val chunkStep = Math.max(Math.floor((chunkSize / 10.0)), 1.0).toInt()

        var prevDataIndex = 0
        var sampledPerChunk = 0F
        var sumPerChunk = 0F

        if (targetSize >= data.size) {
            return targetSized.paste(data)
        }

        for (index in 0..data.size step chunkStep) {
            val currentDataIndex = (targetSize * index.toLong() / data.size).toInt()

            if (prevDataIndex == currentDataIndex) {
                sampledPerChunk += 1
                sumPerChunk += data[index].abs
            } else {
                targetSized[prevDataIndex] = (sumPerChunk / sampledPerChunk).toByte()

                sumPerChunk = 0F
                sampledPerChunk = 0F
                prevDataIndex = currentDataIndex
            }
        }

        return targetSized
    }
}

internal val Byte.abs: Byte
    get() = when (this) {
        Byte.MIN_VALUE -> Byte.MAX_VALUE
        in (Byte.MIN_VALUE + 1..0) -> (-this).toByte()
        else -> this
    }

internal fun ByteArray.paste(other: ByteArray): ByteArray {
    if (size == 0) return byteArrayOf()

    return this.apply {
        forEachIndexed { i, _ ->
            this[i] = other.getOrElse(i, { this[i].abs })
        }
    }
}