package dev.olog.presentation.dialog_set_ringtone

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import dev.olog.domain.executor.IoScheduler
import dev.olog.presentation.R
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.shared.MediaIdHelper
import dev.olog.shared_android.isMarshmallow
import io.reactivex.Single
import org.jetbrains.anko.toast
import javax.inject.Inject
import javax.inject.Named

class SetRingtoneDialogPresenter @Inject constructor(
        private val application: Application,
        private val activity: AppCompatActivity,
        private val mediaId: String,
        @Named("item title") private val itemTitle: String,
        private val scheduler: IoScheduler
) {

    fun execute() {

        if (isMarshmallow()) {
            if (Settings.System.canWrite(application)) {
                setRingtone()
            } else {
                requestWritingSettingsPermission()
            }
        } else {
            setRingtone()
        }
    }

    private fun setRingtone(){
        Single.fromCallable(this::writeSettings)
                .subscribeOn(scheduler.worker)
                .observeOn(scheduler.ui)
                .doOnSuccess { createSuccessMessage() }
                .doOnError { createErrorMessage() }
                .subscribe({}, Throwable::printStackTrace)
    }

    @TargetApi(23)
    private fun requestWritingSettingsPermission(){
        AlertDialog.Builder(activity)
                .setTitle(R.string.popup_permission)
                .setMessage(R.string.popup_request_permission_write_settings)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_ok, { _, _ ->
                    val packageName = application.packageName
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName"))
                    activity.startActivity(intent)
                }).makeDialog()
    }

    private fun createSuccessMessage(){
        val message = application.getString(R.string.song_x_set_as_ringtone, itemTitle)
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

    private fun writeSettings() : Boolean {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

        val values = ContentValues(2)
        values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1")

        application.contentResolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                values, "${BaseColumns._ID} = ?", arrayOf("$songId"))

        return Settings.System.putString(application.contentResolver, Settings.System.RINGTONE, uri.toString())
    }

}