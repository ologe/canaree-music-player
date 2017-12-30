package dev.olog.presentation._base.list

data class AdapterData<out T>(
        val data: T,
        val version: Int
)