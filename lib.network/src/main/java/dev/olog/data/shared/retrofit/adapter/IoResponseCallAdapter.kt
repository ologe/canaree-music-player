package dev.olog.data.shared.retrofit.adapter

import dev.olog.data.shared.retrofit.call.IoResultCall
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class IoResponseCallAdapter<T : Any>(
    private val responseType: Type,
    private val dispatcher: CoroutineDispatcher
) : CallAdapter<T, Any> {

    override fun adapt(call: Call<T>): Any {
        return IoResultCall(dispatcher, 0, call, mutableListOf())
    }

    override fun responseType(): Type = responseType
}