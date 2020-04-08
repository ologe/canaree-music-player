package dev.olog.data.shared.retrofit.call

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class AbsCall<In : Any, Out : Any>(
    protected val dispatcher: CoroutineDispatcher,
    protected val retry: Int = 0,
    protected val delegate: Call<In>
) : Call<Out> {

    private var job: Job? = null

    protected abstract suspend fun enqueueImpl(callback: Callback<Out>)
    protected abstract fun cloneImpl(retry: Int): Call<Out>

    override fun enqueue(callback: Callback<Out>) {
        job?.cancel()
        job = GlobalScope.launch(dispatcher) {
            enqueueImpl(callback)
        }
    }

    override fun clone(): Call<Out> {
        return cloneImpl(retry + 1)
    }

    override fun cancel() {
        job?.cancel()
        delegate.cancel()
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun execute(): Response<Out> {
        throw NotImplementedError()
    }

    override fun request(): Request {
        return delegate.request()
    }
}