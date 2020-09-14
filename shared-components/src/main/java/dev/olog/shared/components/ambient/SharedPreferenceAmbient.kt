package dev.olog.shared.components.ambient

import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ContextAmbient
import androidx.preference.PreferenceManager

@Composable
fun<T> SharedPreferenceAmbient(
    key: String,
    default: String,
    override: T? = null,
    mapper: (String) -> T,
    content: @Composable (T) -> Unit
) {
    val context = ContextAmbient.current

    // TODO use androidx version
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    var currentValue by remember {
        val initialValue = override ?: mapper(default)
        mutableStateOf(initialValue)
    }

    onCommit(context) {

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                currentValue = mapper(prefs.getString(key, default)!!)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }

    }

    content(currentValue)

}