package dev.olog.presentation.activity_splash

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Lazy
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.utils.extension.requestStoragePemission
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.extension.hasPermission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class SplashActivity : BaseActivity() {

    @Inject lateinit var presenter: SplashActivityPresenter
    @Inject lateinit var adapter : Lazy<SplashActivityViewPagerAdapter>
    @Inject lateinit var rxPermissions: RxPermissions
    private val onPageChangeListenerGradientBackground by lazy(NONE) { OnPageChangeListenerGradientBackground(
            viewPager, Color.WHITE, intArrayOf(0xfff79f32.toInt(), 0xfffcca1c.toInt())) }

    private var disposable : Disposable? = null
    private var clickDisposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasStoragePermission = checkStoragePermission()

        if (presenter.isFirstAccess(hasStoragePermission)){
            setContentView(R.layout.activity_splash)
            viewPager.adapter = adapter.get()
            inkIndicator.setViewPager(viewPager)
        } else {
            toMainActivity()
        }

    }

    override fun onResume() {
        super.onResume()
        viewPager.addOnPageChangeListener(onPageChangeListenerGradientBackground)
        setupStorageRequestListener()
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListenerGradientBackground)
        clickDisposable.unsubscribe()
    }

    override fun onStop() {
        super.onStop()
        disposable.unsubscribe()
    }

    private fun setupStorageRequestListener(){
        clickDisposable = RxView.clicks(next)
                .flatMap { if (viewPager.currentItem != 0) {
                    rxPermissions.requestStoragePemission()
                } else {
                    Observable.just(false)
                }}
                .subscribe({ success ->
                    if (success){
                        disposable = presenter.prefetchImages()
                                .doOnSubscribe { showLoader() }
                                .timeout(6, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    loader.pauseAnimation()
                                    toMainActivity()
                                }, {
                                    if (it is TimeoutException){
                                        loader.pauseAnimation()
                                        toMainActivity()
                                    } else {
                                        it.printStackTrace()
                                    }
                                })
                    } else if (viewPager.currentItem == 0){
                        viewPager.setCurrentItem(1, true)
                    }
                }, Throwable::printStackTrace)
    }

    private fun showLoader(){
        viewPager.visibility = View.GONE
        inkIndicator.visibility = View.GONE
        next.visibility = View.GONE

        val messages = resources.getStringArray(R.array.splash_loading_messages)
        val randomMessage = messages[Random().nextInt(messages.size)]

        loader.visibility = View.VISIBLE
        message.visibility = View.VISIBLE
        message.text = randomMessage
        loader.playAnimation()
    }

    private fun checkStoragePermission() : Boolean {
        return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun toMainActivity() {
        startActivity(intentFor<MainActivity>()
                .clearTop()
                .newTask())
        finish()
    }

}