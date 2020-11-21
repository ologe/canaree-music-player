package dev.olog.lib.network.retrofit

import dev.olog.lib.network.ConnectivityManager
import dev.olog.lib.network.model.IoResult
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class IoResultAdapterFactory(
    private val connectivityManager: ConnectivityManager,
    private val dispatcher: CoroutineDispatcher,
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit?
    ): CallAdapter<*, *>? {
        // suspend calls are wrapped into retrofit2.Call
        if (getRawType(returnType) != Call::class.java) {
            return null
        }
        // actual return type, should be IoResult
        val actualReturnType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(actualReturnType) != IoResult::class.java) {
            return null
        }
        val wrapped = getParameterUpperBound(0, actualReturnType as ParameterizedType)
        return IoResultAdapter(dispatcher, connectivityManager, getRawType(wrapped))
    }
}

private class IoResultAdapter(
    private val dispatcher: CoroutineDispatcher,
    private val connectivityManager: ConnectivityManager,
    private val responseType: Type
) : CallAdapter<IoResult<Any>, Any> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<IoResult<Any>>): Any {
        return IoResultCall(dispatcher, connectivityManager, call)
    }
}