package dev.olog.shared.android.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

fun EditText.afterTextChange(): Flow<String> {
    return channelFlow {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                trySend(s!!.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }

        addTextChangedListener(watcher)
        invokeOnClose {
            removeTextChangedListener(watcher)
        }
    }
}