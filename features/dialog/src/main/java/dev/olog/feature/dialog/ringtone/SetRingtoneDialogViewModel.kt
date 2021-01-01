package dev.olog.feature.dialog.ringtone

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.dialog.R
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.utils.isMarshmallow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetRingtoneDialogViewModel @Inject constructor(
    @Assisted private val state: SavedStateHandle,
    private val schedulers: Schedulers,
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString) as MediaId.Track

    @Suppress("IMPLICIT_CAST_TO_ANY")
    suspend fun execute(
        activity: FragmentActivity
    ) = withContext(schedulers.io) {
        if (!isMarshmallow() || (isMarshmallow()) && Settings.System.canWrite(activity)) {
            setRingtone(activity)
        } else {
            requestWritingSettingsPermission(activity)
        }
    }

    @TargetApi(23)
    private suspend fun requestWritingSettingsPermission(
        activity: FragmentActivity
    ) = withContext(schedulers.main) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.popup_permission)
            .setMessage(R.string.popup_request_permission_write_settings)
            .setNegativeButton(R.string.popup_negative_cancel, null)
            .setPositiveButton(R.string.popup_positive_ok) { _, _ ->
                val packageName = activity.packageName
                val intent = Intent(
                    Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                activity.startActivity(intent)
            }.show()
    }

    private fun setRingtone(activity: FragmentActivity): Boolean {
        val songId = mediaId.id
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

        val values = ContentValues(1)
        values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1")

        activity.contentResolver.update(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            values, "${BaseColumns._ID} = ?", arrayOf("$songId")
        )

        return Settings.System.putString(
            activity.contentResolver,
            Settings.System.RINGTONE,
            uri.toString()
        )
    }

}