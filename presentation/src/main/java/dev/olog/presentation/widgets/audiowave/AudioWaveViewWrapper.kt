package dev.olog.presentation.widgets.audiowave

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import dev.olog.presentation.R
import java.io.File

class AudioWaveViewWrapper @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : FrameLayout(context, attrs) {

    private val audioBar : AudioWaveView
    private val progressBar: ProgressBar

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_audio_view, null, false)
        addView(view)
        audioBar = view.findViewById(R.id.wave)
        progressBar = view.findViewById(R.id.progressBar)
    }

    fun onTrackChanged(path: String){
        try {
            val file = File(path)
            audioBar.setRawData(file.readBytes())
        } catch (ex: OutOfMemoryError){
            ex.printStackTrace()
        }

    }

    fun updateProgress(progress: Int){
        progressBar.progress = progress
        val relativeProgress = 100f * progress.toFloat() / progressBar.max.toFloat()
        audioBar.progress = relativeProgress
    }

    fun updateMax(max: Long){
        this.progressBar.max = max.toInt()
    }

}