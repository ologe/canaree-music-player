package dev.olog.msc.presentation.splash

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseActivity
import dev.olog.msc.utils.k.extension.makeDialog
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject

private const val STORAGE_PERMISSION_CODE = 56891

class SplashActivity : BaseActivity(), View.OnClickListener {

    @Inject lateinit var presenter: SplashActivityPresenter
    @Inject lateinit var adapter : SplashActivityViewPagerAdapter
    private var disposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewPager.adapter = adapter
        inkIndicator.setViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        next.setOnClickListener {
            if (viewPager.currentItem == 0){
                viewPager.setCurrentItem(1, true)
            } else {
                requestStoragePermission()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        next.setOnClickListener(null)
    }

    override fun onDestroy() {
        disposable.unsubscribe()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        if (viewPager.currentItem == 0){
            viewPager.setCurrentItem(1, true)
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onStoragePermissionGranted()
                } else {
                    onStoragePermissionDenied()
                }
            }
        }
    }

    private fun finishActivity(){
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun onStoragePermissionGranted(){
        disposable = presenter.prefetchImages()
                .doOnSubscribe { showLoader() }
                .timeout(6, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loader.pauseAnimation()
                    finishActivity()
                }, {
                    if (it is TimeoutException){
                        loader.pauseAnimation()
                        finishActivity()
                    } else {
                        throw RuntimeException("something went very wrong", it)
                    }
                })
    }

    private fun onStoragePermissionDenied(){
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // user disabled permission
            AlertDialog.Builder(this)
                    .setTitle(R.string.splash_storage_permission)
                    .setMessage(R.string.splash_storage_permission_disabled)
                    .setPositiveButton(R.string.popup_positive_ok, { _, _ -> toSettings() })
                    .setNegativeButton(R.string.popup_negative_no, null)
                    .makeDialog()
        }
    }

    private fun toSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null))
        startActivity(intent)
    }

    private fun showLoader(){
        viewPager.visibility = View.GONE
        inkIndicator.visibility = View.GONE
        next.visibility = View.GONE

        loader.visibility = View.VISIBLE
        loader.playAnimation()
    }

}