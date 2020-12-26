package dev.olog.test.shared

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class LifecycleOwnerRule : TestWatcher() {

    val owner = TestLifecycleOwner()

    override fun starting(description: Description) {
        super.starting(description)
        owner.moveToState(Lifecycle.State.CREATED)
        owner.moveToState(Lifecycle.State.STARTED)
        owner.moveToState(Lifecycle.State.RESUMED)
    }

    override fun finished(description: Description) {
        super.finished(description)
        owner.moveToState(Lifecycle.State.STARTED)
        owner.moveToState(Lifecycle.State.CREATED)
        owner.moveToState(Lifecycle.State.DESTROYED)
    }
}

class TestLifecycleOwner : LifecycleOwner {

    private val internalLifecycle = object : Lifecycle() {

        var state = State.INITIALIZED

        override fun addObserver(observer: LifecycleObserver) {}

        override fun removeObserver(observer: LifecycleObserver) {}

        override fun getCurrentState(): State = state
    }

    fun moveToState(state: Lifecycle.State) {
        internalLifecycle.state = state
    }

    override fun getLifecycle(): Lifecycle = internalLifecycle
}