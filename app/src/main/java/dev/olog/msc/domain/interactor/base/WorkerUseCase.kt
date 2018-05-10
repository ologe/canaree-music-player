package dev.olog.msc.domain.interactor.base

import androidx.work.InputMerger
import androidx.work.OneTimeWorkRequest
import androidx.work.OverwritingInputMerger
import androidx.work.Worker
import androidx.work.ktx.OneTimeWorkRequestBuilder
import androidx.work.ktx.setInputMerger
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

abstract class WorkerUseCase {

    fun execute(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<Work>()
                .setInitialDelay(millisDelay, TimeUnit.MILLISECONDS)
                .setInputMerger(inputMerger)
                .build()
    }

    inner class Work : Worker() {

        override fun doWork(): WorkerResult {
            return buildUseCase()
        }

    }

    protected abstract fun buildUseCase(): Worker.WorkerResult

    protected open val millisDelay = 0L

    protected open val inputMerger: KClass<out InputMerger> = OverwritingInputMerger::class

}