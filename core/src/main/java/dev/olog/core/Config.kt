package dev.olog.core

data class Config(
    val isDebug: Boolean,
    val versionName: String,
    val versionCode: Int,
    val lastFmKey: String,
)