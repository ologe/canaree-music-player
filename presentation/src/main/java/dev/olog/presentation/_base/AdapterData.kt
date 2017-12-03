package dev.olog.presentation._base

data class AdapterData<out T>(
        val data: T,
        val version: Int
)