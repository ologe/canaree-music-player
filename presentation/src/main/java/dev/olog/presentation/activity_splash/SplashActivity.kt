package dev.olog.presentation.activity_splash

import android.Manifest
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
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

    @Inject lateinit var presenter: SplashPresenter
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
            window.navigationBarColor = ContextCompat.getColor(this, R.color.toolbar)
        }
    }

    override fun onResume() {
        super.onResume()
        val pager = findViewById<ViewPager>(R.id.viewPager)
        pager?.addOnPageChangeListener(onPageChangeListener)
    }

    override fun onPause() {
        super.onPause()
        val pager = findViewById<ViewPager>(R.id.viewPager)
        pager?.removeOnPageChangeListener(onPageChangeListener)
    }

    override fun onDestroy() {
        disposable.unsubscribe()
        super.onDestroy()
    }

    private fun checkStoragePermission() : Boolean {
        return hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            disposable.unsubscribe()

            if (position == 1){
                disposable = presenter.subscribeToStoragePermission(root)
                        .subscribe({ navigator.toMainActivity() }, Throwable::printStackTrace)
            }

        }
    }


}