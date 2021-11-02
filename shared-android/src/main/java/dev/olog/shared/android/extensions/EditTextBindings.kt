package dev.olog.shared.android.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.flow.*

fun EditText.afterTextChangeFlow(): Flow<String> {
    val flow = MutableStateFlow<String?>(null)

    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            flow.value = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    return flow
        .onStart { addTextChangedListener(watcher) }
        .onCompletion { removeTextChangedListener(watcher) }
        .filterNotNull()
}