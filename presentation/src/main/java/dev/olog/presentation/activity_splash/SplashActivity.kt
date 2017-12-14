package dev.olog.presentation.activity_splash

import android.Manifest
import android.os.Bundle
import dagger.Lazy
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.extension.hasPermission
import dev.olog.presentation.utils.extension.subscribe
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    @Inject lateinit var presenter: SplashPresenter
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var adapter : Lazy<SplashActivityViewPagerAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasStoragePermission = checkStoragePermission()

        if (presenter.isFirstAccess(hasStoragePermission)){
            setContentView(R.layout.activity_splash)
            viewPager.adapter = adapter.get()
            subscribeToStorageRequest()
        } else {
            navigator.toMainActivity()
        }
    }

    private fun checkStoragePermission() : Boolean {
        return hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun subscribeToStorageRequest(){
        presenter.subscribeToStoragePermission(root)
                .subscribe(this, { navigator.toMainActivity() })
    }

}