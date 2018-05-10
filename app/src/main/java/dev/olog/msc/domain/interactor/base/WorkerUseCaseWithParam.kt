package dev.olog.msc.domain.interactor.base

import androidx.work.*
import androidx.work.ktx.OneTimeWorkRequestBuilder
import androidx.work.ktx.setInputMerger
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

abstract class WorkerUseCaseWithParam {

    fun execute(inputData: Map<String, Any>): OneTimeWorkRequest {

        return OneTimeWorkRequestBuilder<Work>()
                .setInitialDelay(millisDelay, TimeUnit.MILLISECONDS)
                .setInputMerger(inputMerger)
                .setInputData(inputData.toInputData())
                .build()
    }

    inner class Work : Worker() {

        override fun doWork(): WorkerResult {
            return buildUseCase(inputData)
        }

    }

    protected abstract fun buildUseCase(input: Data): Worker.WorkerResult

    protected open val millisDelay = 0L

    protected open val inputMerger: KClass<out InputMerger> = OverwritingInputMerger::class

    private fun Map<String, Any>.toInputData(): Data {
        val builder = Data.Builder()

        for (entry in this) {
            when (entry.value){
                is Int -> builder.putInt(entry.key, entry.value as Int)
                is Long -> builder.putLong(entry.key, entry.value as Long)
                is String -> builder.putString(entry.key, entry.value as String)
                is Float -> builder.putFloat(entry.key, entry.value as Float)
                is Double -> builder.putDouble(entry.key, entry.value as Double)
                is Boolean -> builder.putBoolean(entry.key, entry.value as Boolean)
            }
        }
        return builder.build()
    }

}