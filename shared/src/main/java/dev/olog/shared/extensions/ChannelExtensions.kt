package dev.olog.shared.extensions

import kotlinx.coroutines.channels.SendChannel

suspend fun <T> SendChannel<T>.safeSend(element: T) {
    if (!isClosedForSend) {
        send(element)
    }
}