package dev.olog.core

data class Config(
    val isDebug: Boolean,
    val versionCode: Int,
    val versionName: String,
    val lastFmBaseUrl: String,
    val lastFmKey: String,
    val lastFmSecret: String,
    val aesPassword: String,
)