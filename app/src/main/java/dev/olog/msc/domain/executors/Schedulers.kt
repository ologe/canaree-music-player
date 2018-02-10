package dev.olog.msc.domain.executors

import io.reactivex.Scheduler

interface Schedulers {

    val worker: Scheduler
    val ui: Scheduler

}