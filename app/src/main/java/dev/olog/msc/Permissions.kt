package dev.olog.msc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

object Permissions {

    private const val READ_CODE = 100

    private const val READ_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
//    private const val WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

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