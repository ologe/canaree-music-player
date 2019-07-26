package dev.olog.msc

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import dev.olog.image.provider.HasGlideSignature
import dev.olog.shared.mutableLazy
import javax.inject.Inject

class GlideSignatureListener @Inject constructor(
    private val prefs: SharedPreferences

) : HasGlideSignature, DefaultLifecycleObserver {

    companion object {
        private const val KEY = "GlideSignatureListener"
    }

    private var currentValue by mutableLazy {
        prefs.getInt(KEY, 0)
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun getCurrentVersion(): Int = currentValue

    override fun increaseCurrentVersion() {
        currentValue++
        prefs.edit {
            putInt(KEY, currentValue + 1)
        }
    }
}