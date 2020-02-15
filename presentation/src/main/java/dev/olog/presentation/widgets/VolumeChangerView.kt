package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class VolumeChangerView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs), CoroutineScope by MainScope() {

    init {
        setImageResource(R.drawable.vd_volume_up)
    }

    var musicPrefs: MusicPreferencesGateway? = null
        set(value) {
            field = value
            if (value != null) {
                startObserving()
            }
        }

    private var job by autoDisposeJob()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode){
            musicPrefs?.let { startObserving() }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job = null
    }

    private fun startObserving() {
        job = musicPrefs!!.observeVolume()
            .flowOn(Dispatchers.Default)
            .onEach { updateImage(it) }
            .launchIn(this)
    }

    private fun updateImage(volume: Int) {
        val drawable = when (volume) {
            0 -> R.drawable.vd_volume_mute
            in 1..60 -> R.drawable.vd_volume_down
            else -> R.drawable.vd_volume_up
        }
        setImageResource(drawable)
    }

}