package dev.olog.shared.android.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import dev.olog.shared.android.utils.assertMainThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

fun EditText.afterTextChange(): Flow<String> {
    assertMainThread()
    val flow = MutableStateFlow(text.toString())

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
}