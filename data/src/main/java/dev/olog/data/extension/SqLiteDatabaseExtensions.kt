package dev.olog.data.extension

import android.content.ContentValues
import androidx.sqlite.db.SupportSQLiteDatabase

internal fun SupportSQLiteDatabase.transaction(
    action: SupportSQLiteDatabase.() -> Unit
) {
    try {
        beginTransaction()
        action()
        setTransactionSuccessful()
    } finally {
        endTransaction()
    }
}

internal fun SupportSQLiteDatabase.insertMultiple(
    tableName: String,
    conflictAlgorithm: Int,
    vararg values: ContentValues
) {
    for (value in values) {
        insert(tableName, conflictAlgorithm, value)
    }
}