package dev.olog.presentation.utils.extension

import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable

@Suppress("SimplifyBooleanWithConstants")
fun RxPermissions.requestStoragePemission() : Observable<Boolean> {
    return this.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
}