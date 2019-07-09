package dev.olog.shared.debug

import android.util.Log
import dev.olog.shared.BuildConfig

interface Loggable {
    fun logVerbose(msg: String)
    fun logDebug(msg: String)
    fun logInfo(msg: String)
    fun logWarning(msg: String)
    fun logError(msg: String)
}

enum class LogLevel{
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR
}

class LoggableImpl(
    private val tag: String,
    private val level: LogLevel
) : Loggable {

    override fun logVerbose(msg: String) {
        if (BuildConfig.DEBUG && level.ordinal <= LogLevel.VERBOSE.ordinal){
            Log.v(tag, msg)
        }
    }

    override fun logDebug(msg: String) {
        if (BuildConfig.DEBUG && level.ordinal <= LogLevel.DEBUG.ordinal){
            Log.d(tag, msg)
        }
    }

    override fun logInfo(msg: String) {
        if (BuildConfig.DEBUG && level.ordinal <= LogLevel.INFO.ordinal){
            Log.i(tag, msg)
        }
    }

    override fun logWarning(msg: String) {
        if (BuildConfig.DEBUG && level.ordinal <= LogLevel.WARNING.ordinal){
            Log.w(tag, msg)
        }
    }

    override fun logError(msg: String) {
        if (BuildConfig.DEBUG && level.ordinal <= LogLevel.ERROR.ordinal){
            Log.e(tag, msg)
        }
    }
}