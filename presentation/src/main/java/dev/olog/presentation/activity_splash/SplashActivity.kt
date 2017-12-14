package dev.olog.presentation.activity_splash

import android.Manifest
import android.os.Bundle
import android.support.v4.content.ContextCompat
import dagger.Lazy
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.extension.hasPermission
import dev.olog.presentation.utils.isOreo
import dev.olog.shared.unsubscribe
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    @Inject lateinit var presenter: SplashActivityPresenter
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var adapter : Lazy<SplashActivityViewPagerAdapter>

    private var disposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasStoragePermission = checkStoragePermission()

        if (presenter.isFirstAccess(hasStoragePermission)){
            setContentView(R.layout.activity_splash)
            viewPager.adapter = adapter.get()
            inkIndicator.setViewPager(viewPager)
        } else {
            navigator.toMainActivity()
        }

        if (isOreo()){
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
    }

    override fun onResume() {
        super.onResume()
        next.setOnClickListener {
            if (viewPager.currentItem == 0){
                viewPager.currentItem = 1
            } else {
                disposable.unsubscribe()
                disposable = presenter.subscribeToStoragePermission(it)
                        .subscribe({ navigator.toMainActivity() }, Throwable::printStackTrace)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        next.setOnClickListener(null)
        disposable.unsubscribe()
    }

    private fun checkStoragePermission() : Boolean {
        return hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

}