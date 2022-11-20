package dev.olog.data.network

import dev.olog.core.IoResult
import okhttp3.Request
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class IoResultCall<T : Any>(
    private val proxy: Call<T>
) : Call<IoResult<T>> {

    override fun enqueue(callback: Callback<IoResult<T>>) {
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    callback.onResponse(
                        this@IoResultCall,
                        Response.success(IoResult.Success(response.body()!!))
                    )
                } else {
                    @Suppress("ThrowableNotThrown")
                    val httpException = HttpException(response)
                    callback.onResponse(
                        this@IoResultCall,
                        Response.success(
                            IoResult.Failure.Http(
                                status = httpException.code(),
                                message = "${httpException.message()} - url ${call.request().url}",
                                exception = httpException,
                            )
                        )
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val response = if (t is IOException) {
                    IoResult.Failure.Network(t)
                } else {
                    IoResult.Failure.Unknown(t)
                }
                callback.onResponse(
                    this@IoResultCall,
                    Response.success(response)
                )
            }
        })
    }

    override fun execute(): Response<IoResult<T>> {
        error("blocking calls are not allowed")
    }

    override fun clone(): Call<IoResult<T>> = IoResultCall(proxy.clone())
    override fun request(): Request = proxy.request()
    override fun timeout(): Timeout = proxy.timeout()
    override fun isExecuted(): Boolean = proxy.isExecuted
    override fun isCanceled(): Boolean = proxy.isCanceled
    override fun cancel() = proxy.cancel()
}