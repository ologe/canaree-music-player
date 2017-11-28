package dev.olog.presentation._base

data class AdapterData<out T>(
        val list: T,
        val dataVersion : Int
)