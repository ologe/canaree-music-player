package dev.olog.domain.executor

import io.reactivex.Scheduler

interface Schedulers {

    val worker: Scheduler
    val ui: Scheduler

}