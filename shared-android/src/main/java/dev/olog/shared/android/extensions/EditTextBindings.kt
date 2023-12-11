package dev.olog.shared.android.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

fun EditText.afterTextChange(): Flow<String> {
    val channel = MutableSharedFlow<String>()

    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            channel.tryEmit(s?.toString().orEmpty())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    return channel
        // TODO check if is correct
        .onStart { addTextChangedListener(watcher) }
        .onCompletion { removeTextChangedListener(watcher) }
        .filterNotNull()
}