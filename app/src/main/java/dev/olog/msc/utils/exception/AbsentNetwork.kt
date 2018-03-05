package dev.olog.msc.utils.exception

class AbsentNetwork : RuntimeException()

fun noNetwork(): Nothing = throw AbsentNetwork()