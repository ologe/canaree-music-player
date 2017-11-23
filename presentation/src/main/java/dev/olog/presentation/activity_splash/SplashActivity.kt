package dev.olog.presentation.activity_splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.asLiveData
import dev.olog.presentation.utils.requestStoragePemission
import dev.olog.presentation.utils.subscribe
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    private val rxPermission by lazy(LazyThreadSafetyMode.NONE) { RxPermissions(this) }

    @Inject lateinit var presenter: SplashPresenter
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasStoragePermission = checkStoragePermission()

        if (presenter.isFirstAccess(hasStoragePermission)){
            setContentView(R.layout.activity_splash)
            subscribeToStorageRequest()
        } else {
            navigator.toMainActivity()
        }
    }

    private fun checkStoragePermission() : Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun subscribeToStorageRequest(){
        RxView.clicks(root)
                .flatMap { rxPermission.requestStoragePemission() }
                .asLiveData()
                .subscribe(this, { navigator.toMainActivity() })
    }

}