package dev.olog.msc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.olog.msc.presentation.utils.lazyFast
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

private val hasPermissionPublisher by lazyFast { BehaviorSubject.createDefault<Boolean>(false) }

fun updatePermissionValve(context: Context, enable: Boolean){
    hasPermissionPublisher.onNext(enable)
    context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    context.contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
    context.contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
}

fun <T> Observable<T>.onlyWithStoragePermission(): Observable<T> {
    return hasPermissionPublisher.filter { it }
            .switchMap { this }
}

object Permissions {

    private const val READ_CODE = 100

    private const val READ_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

    fun checkWriteCode(code: Int): Boolean {
        return code == READ_CODE
    }

    fun canReadStorage(context: Context): Boolean {
        return hasPermission(context, READ_STORAGE)
    }

    fun requestReadStorage(activity: Activity){
        requestPermissions(activity, READ_STORAGE, READ_CODE)
    }

    fun hasUserDisabledReadStorage(activity: Activity): Boolean {
        return hasUserDisabledPermission(activity, READ_STORAGE)
    }

    private fun hasPermission(context: Context, permission: String): Boolean{
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(activity: Activity, permission: String, requestCode: Int){
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    private fun hasUserDisabledPermission(activity: Activity, permission: String): Boolean{
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

}