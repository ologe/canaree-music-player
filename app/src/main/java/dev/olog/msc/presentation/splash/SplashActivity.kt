package dev.olog.msc.presentation.splash

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import dev.olog.shared.Permissions
import dev.olog.msc.R
import dev.olog.presentation.base.BaseActivity
import dev.olog.msc.presentation.dialog.explain.trial.ExplainTrialDialog
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.extensions.alertDialog
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity(), View.OnClickListener {

    private val adapter by lazyFast { SplashActivityViewPagerAdapter(supportFragmentManager) }

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

    override fun onClick(v: View?) {
        if (viewPager.currentItem == 0){
            viewPager.setCurrentItem(1, true)
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission(){
        if (!Permissions.canReadStorage(this)){
            Permissions.requestReadStorage(this)
        } else {
            onStoragePermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Permissions.checkWriteCode(requestCode)){
            if (Permissions.canReadStorage(this)){
                onStoragePermissionGranted()
            } else {
                onStoragePermissionDenied()
            }
        }
    }

    private fun onStoragePermissionGranted(){
        ExplainTrialDialog.show(this) {
            finishActivity()
        }
    }

    private fun finishActivity(){
        setResult(Activity.RESULT_OK)
        finish()
    }


    private fun onStoragePermissionDenied(){
        if (Permissions.hasUserDisabledReadStorage(this)){
            alertDialog {
                setTitle(R.string.splash_storage_permission)
                setMessage(R.string.splash_storage_permission_disabled)
                setPositiveButton(R.string.popup_positive_ok, { _, _ -> toSettings() })
                setNegativeButton(R.string.popup_negative_no, null)
            }
        }
    }

    private fun toSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null))
        startActivity(intent)
    }

}