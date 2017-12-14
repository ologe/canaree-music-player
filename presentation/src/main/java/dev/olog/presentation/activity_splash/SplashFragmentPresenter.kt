package dev.olog.presentation.activity_splash

import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import dev.olog.presentation.utils.extension.requestStoragePemission
import io.reactivex.Observable
import javax.inject.Inject

class SplashFragmentPresenter @Inject constructor(
        private val rxPermissions: RxPermissions

) {

    fun subscribeToStoragePermission(view: View): Observable<Boolean> {
        return RxView.clicks(view)
                .flatMap { rxPermissions.requestStoragePemission() }
    }

}