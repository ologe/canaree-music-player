package dev.olog.shared

import kotlinx.coroutines.channels.SendChannel

suspend fun <T> SendChannel<T>.safeSend(element: T) {
    if (!isClosedForSend) {
        send(element)
    }
}