package dev.olog.data.network

import dev.olog.core.Try
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class TryCallAdapter(
    private val type: Type,
) : CallAdapter<Type, Call<Try<Type>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<Type>): Call<Try<Type>> = TryCall(call)

    class Factory : CallAdapter.Factory() {

        override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *>? {
            if (getRawType(returnType) != Call::class.java || returnType !is ParameterizedType) return null

            val resultType = getParameterUpperBound(0, returnType)
            if (getRawType(resultType) != Try::class.java || resultType !is ParameterizedType) return null

            val paramType = getParameterUpperBound(0, resultType)
            return TryCallAdapter(paramType)
        }

        fun create(): CallAdapter.Factory {
            return Factory()
        }

    }

}