package dev.olog.presentation.activity_splash

import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import dev.olog.domain.interactor.splash.FirstAccessUseCase
import dev.olog.presentation.utils.extension.requestStoragePemission
import io.reactivex.Observable
import javax.inject.Inject

class SplashPresenter @Inject constructor(
        private val firstAccessUseCase : FirstAccessUseCase,
        private val rxPermissions: RxPermissions
) {

    fun isFirstAccess(hasStoragePermission: Boolean): Boolean {
        val isFirstAccess = firstAccessUseCase.get()
        return isFirstAccess || !hasStoragePermission
    }

    fun subscribeToStoragePermission(view: View): Observable<Boolean> {
        return RxView.clicks(view)
                .flatMap { rxPermissions.requestStoragePemission() }
    }

}