package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@AndroidEntryPoint
class VolumeChangerView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    init {
        setImageResource(R.drawable.vd_volume_up)
    }

    @Inject
    lateinit var musicPrefs: MusicPreferencesGateway

    private var job: Job? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode){
            startObserving()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode){
            job?.cancel()
        }
    }

    private fun startObserving() {
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Main) {
            musicPrefs.observeVolume()
                .flowOn(Dispatchers.Default)
                .collect { updateImage(it) }
        }
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