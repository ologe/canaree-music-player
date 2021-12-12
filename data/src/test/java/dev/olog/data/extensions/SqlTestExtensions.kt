package dev.olog.data.extensions

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.TransactionWithoutReturn

@Suppress("TestFunctionName")
fun <T : Any> QueryList(value: List<T>): Query<T> {
    return mock { on { executeAsList() } doReturn value }
}

@Suppress("TestFunctionName")
fun <T : Any> QueryList(vararg values: T): Query<T> {
    return mock { on { executeAsList() } doReturn values.toList() }
}

@Suppress("TestFunctionName")
fun <T : Any> QueryOne(value: T): Query<T> {
    return mock {
        on { executeAsOne() } doReturn value
    }
}

@Suppress("TestFunctionName")
fun <T : Any> QueryOneOrNull(value: T?): Query<T> {
    return mock { on { executeAsOneOrNull() } doReturn value }
}

// TODO not really happy about this
fun mockTransacter(transactor: Transacter) {
    whenever(transactor.transaction(any(), any()))
        .thenAnswer {
            it.getArgument<TransactionWithoutReturn.() -> Unit>(1).invoke(mock())
        }
}