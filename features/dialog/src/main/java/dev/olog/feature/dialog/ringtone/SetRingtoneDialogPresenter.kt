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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.dialog.R
import dev.olog.shared.android.utils.isMarshmallow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetRingtoneDialogPresenter @Inject constructor() {

    @Suppress("IMPLICIT_CAST_TO_ANY")
    suspend fun execute(activity: FragmentActivity, mediaId: MediaId) =
        withContext(Dispatchers.IO) {
            if (!isMarshmallow() || (isMarshmallow()) && Settings.System.canWrite(activity)) {
                setRingtone(activity, mediaId)
            } else {
                requestWritingSettingsPermission(activity)
            }
        }

    @TargetApi(23)
    private suspend fun requestWritingSettingsPermission(activity: FragmentActivity) =
        withContext(Dispatchers.Main) {
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

    private fun setRingtone(activity: FragmentActivity, mediaId: MediaId): Boolean {
        val songId = mediaId.leaf!!
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