package dev.olog.compose.composition.local

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager

@Composable
fun <T : Any> LocalPreference(
    key: String,
    serialize: Context.(T) -> String,
    deserialize: Context.(String) -> T,
    default: T,
    override: T?,
    providableCompositionLocal: ProvidableCompositionLocal<T>,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val prefs = remember(context) { PreferenceManager.getDefaultSharedPreferences(context.applicationContext) }

    var state: T by remember(override) {
        val value = override ?: deserialize(context, prefs.getString(key, serialize(context, default))!!)
        mutableStateOf(value)
    }

    DisposableEffect(context, key) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { p, k ->
            if (key == k) {
                state = deserialize(context, p.getString(key, serialize(context, default))!!)
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    CompositionLocalProvider(providableCompositionLocal provides state) {
        content()
    }
}