package dev.olog.lib.network.retrofit

import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal abstract class BaseCall<In, Out>(
    protected val dispatcher: CoroutineDispatcher,
    private val delegate: Call<In>
) : Call<Out> {

    private var job by autoDisposeJob()

    protected abstract suspend fun enqueueImpl(callback: Callback<Out>)
    protected abstract fun cloneImpl(): Call<Out>

    override fun enqueue(callback: Callback<Out>) {
        job = GlobalScope.launch(dispatcher) {
            enqueueImpl(callback)
        }
    }

    override fun clone(): Call<Out> = cloneImpl()

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() {
        job = null
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    override fun execute(): Response<Out> {
        throw UnsupportedOperationException()
    }
}
