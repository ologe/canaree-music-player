package dev.olog.shared.android.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

fun EditText.afterTextChange(): Flow<String> {
    val channel = ConflatedBroadcastChannel<String>()

    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!channel.isClosedForSend) {
                channel.trySend(s!!.toString())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    addTextChangedListener(watcher)
    channel.invokeOnClose {
        removeTextChangedListener(watcher)
    }
    return channel.asFlow()
}