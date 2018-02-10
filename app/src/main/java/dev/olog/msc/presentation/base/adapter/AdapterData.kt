package dev.olog.msc.presentation.base.adapter

data class AdapterData<out T>(
        val data: T,
        val version: Int
)