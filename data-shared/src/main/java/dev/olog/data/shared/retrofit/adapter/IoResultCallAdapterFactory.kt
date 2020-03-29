package dev.olog.data.shared.retrofit.adapter

import dev.olog.data.shared.retrofit.IoResult
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class IoResultCallAdapterFactory(
    private val dispatcher: CoroutineDispatcher
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) == Call::class.java) {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            if (getRawType(callType) == IoResult::class.java) {
                val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                return IoResponseCallAdapter<Any>(getRawType(resultType), dispatcher)
            }
        }
        return null
    }
}

