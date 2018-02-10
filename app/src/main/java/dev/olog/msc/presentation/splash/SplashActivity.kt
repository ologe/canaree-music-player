package dev.olog.msc.presentation.splash

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.view.View
import dagger.Lazy
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseActivity
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.utils.k.extension.makeDialog
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.shared_android.extension.hasPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.intentFor
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

private const val STORAGE_PERMISSION_CODE = 56891

class SplashActivity : BaseActivity() {

    @Inject lateinit var presenter: SplashActivityPresenter
    @Inject lateinit var adapter : Lazy<SplashActivityViewPagerAdapter>
    private val onPageChangeListenerGradientBackground by lazy(NONE) { OnPageChangeListenerGradientBackground(
            viewPager, Color.WHITE, intArrayOf(0xfff79f32.toInt(), 0xfffcca1c.toInt())) }

    private var disposable : Disposable? = null
    private var permissionDisposable: Disposable? = null

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
        next?.setOnClickListener {
            if (viewPager.currentItem == 0){
                viewPager.setCurrentItem(1, true)
            } else {
                requestStoragePermission()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListenerGradientBackground)
        permissionDisposable.unsubscribe()
        next?.setOnClickListener(null)
    }

    override fun onDestroy() {
        disposable.unsubscribe()
        super.onDestroy()
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()){
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        //permission granted
                        startPrefetchingImages()
                    } else {
                        // permission denied
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            // user disabled permission
                            AlertDialog.Builder(this)
                                    .setTitle(R.string.splash_storage_permission_disabled)
                                    .setPositiveButton(R.string.popup_positive_ok, { _, _ ->
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
                                        startActivity(intent)
                                    })
                                    .setNegativeButton(R.string.popup_negative_no, null)
                                    .makeDialog()
                        }
                    }
                }
            }
        }
    }

    private fun startPrefetchingImages(){
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
                        throw RuntimeException("something went very wrong", it)
                    }
                })
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
        startActivity(intentFor<MainActivity>()/*.clearTop().newTask()*/)
        finish()
    }

}