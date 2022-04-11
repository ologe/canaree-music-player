package dev.olog.core

data class Config(
    val isDebug: Boolean,
    val versionName: String,
    val versionCode: Int,
    val aesPassword: String,
    val lastFmBaseUrl: String,
    val lastFmKey: String,
    val lastFmSecret: String,
)