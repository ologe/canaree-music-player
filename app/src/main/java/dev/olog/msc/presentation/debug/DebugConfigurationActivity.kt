package dev.olog.msc.presentation.debug

import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.utils.k.extension.configuration
import kotlinx.android.synthetic.main.activity_debug_configuration.*

class DebugConfigurationActivity: AppCompatActivity() {

//    companion object {
//        var clipStart = 0L
//        var clipEnd = 0L
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug_configuration)

        dpi.append(configuration.densityDpi.toString())
        fontScale.append(configuration.fontScale.toString())
        mcc.append(configuration.mcc.toString())
        mnc.append(configuration.mnc.toString())
        orientation.append(configuration.orientation.toString())
        screenHeight.append(configuration.screenHeightDp.toString())
        screenWidth.append(configuration.screenWidthDp.toString())
        smallestWidth.append(configuration.smallestScreenWidthDp.toString())
        activateFakeData.isChecked = AppConstants.useFakeData

//        seekBarClipStart.progress = clipStart.toInt()
//        seekBarClipEnd.progress = clipEnd.toInt()
    }

    override fun onResume() {
        super.onResume()
        activateFakeData.setOnCheckedChangeListener { _, isChecked ->
            AppConstants.useFakeData = isChecked
            contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
            contentResolver.notifyChange(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null)
            contentResolver.notifyChange(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null)
            contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
            contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
        }

//        seekBarClipStart.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                clipStart = seekBar.progress.toLong()
//            }
//        })
//        seekBarClipEnd.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                clipEnd = seekBar.progress.toLong()
//            }
//        })
    }

    override fun onPause() {
        super.onPause()
        activateFakeData.setOnClickListener(null)
//        seekBarClipStart.setOnSeekBarChangeListener(null)
//        seekBarClipEnd.setOnSeekBarChangeListener(null)
    }

}