package dev.olog.shared.compose.theme

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dev.olog.shared.compose.R

@Composable
internal fun rememberAccentColor(): State<Color> {
    if (LocalInspectionMode.current) {
        val color = colorResource(R.color.defaultColorAccent)
        return remember { mutableStateOf(color) }
    }

    val context = LocalContext.current
    val state = remember(context) { mutableStateOf(Color(getAccentColor(context))) }
    DisposableEffect(Unit) {
        val listener = OnSharedPreferenceChangeListener { _, key ->
            if (key == context.getString(R.string.prefs_color_accent_key)) {
                state.value = Color(getAccentColor(context))
            }
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    return state
}

private fun getAccentColor(context: Context): Int = with(context) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getInt(
        context.getString(R.string.prefs_color_accent_key),
        ContextCompat.getColor(context, R.color.defaultColorAccent)
    )
}