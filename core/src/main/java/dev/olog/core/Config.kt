package dev.olog.core

data class Config(
    val isDebug: Boolean,
    val versionName: String,
    val lastFmKey: String,
    val lastFmSecret: String,
    val aesPassword: String,
)