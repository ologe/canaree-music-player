package dev.olog.data.network

import dev.olog.core.Try
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class TryCall<T : Any>(
    private val proxy: Call<T>
) : Call<Try<T>> {

    override fun enqueue(callback: Callback<Try<T>>) {
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    callback.onResponse(
                        this@TryCall,
                        Response.success(Try.success(response.body()!!))
                    )
                } else {
                    callback.onResponse(
                        this@TryCall,
                        Response.success(Try.failure(HttpException(response)))
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@TryCall,
                    Response.success(Try.failure(t))
                )
            }
        })
    }

    override fun execute(): Response<Try<T>> {
        error("blocking calls are not allowed")
    }

    override fun clone(): Call<Try<T>> = TryCall(proxy.clone())
    override fun isExecuted(): Boolean = proxy.isExecuted
    override fun cancel() = proxy.cancel()
    override fun isCanceled(): Boolean = proxy.isCanceled
    override fun request(): Request = proxy.request()
    override fun timeout(): Timeout = proxy.timeout()
}