package dev.olog.msc.utils.exception

class NoNetworkException : RuntimeException()

fun noNetwork(): Nothing = throw NoNetworkException()