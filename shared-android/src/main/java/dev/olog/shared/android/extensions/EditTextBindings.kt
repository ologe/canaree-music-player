package dev.olog.shared.android.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import dev.olog.shared.android.utils.assertMainThread
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion

fun EditText.afterTextChange(): Flow<String> {
    assertMainThread()
    val channel = ConflatedBroadcastChannel<String>()

    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!channel.isClosedForSend) {
                channel.offer(s!!.toString())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    addTextChangedListener(watcher)
    channel.invokeOnClose {
        assertMainThread()
        removeTextChangedListener(watcher)
    }
    return channel.asFlow()
        .onCompletion { channel.close() }
}