@file:Suppress("DEPRECATION")

package dev.olog.presentation.edit

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.bumptech.glide.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dev.olog.core.MediaId
import dev.olog.core.Stylizer
import dev.olog.core.gateway.getImageVersionGateway
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.CustomMediaStoreSignature
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.utils.NetworkUtils
import dev.olog.shared.lazyFast
import kotlinx.coroutines.*

abstract class BaseEditItemFragment : BaseBottomSheetFragment(),
    CoroutineScope by MainScope() {

    companion object {
        private val TAG = "P:${BaseEditItemFragment::class.java.simpleName}"
        private const val FEATURE_STYLIZE = "feature_stylize"
        private const val FEATURE_STYLIZE_REQUEST_CODE = 152
    }

    private var progressDialog: ProgressDialog? = null

    private val splitManager: SplitInstallManager by lazyFast {
        SplitInstallManagerFactory.create(requireContext())
    }

    private var sessionId: Int? = null

    protected fun loadImage(mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

        launch {
            val version = withContext(Dispatchers.Default){
                requireContext().getImageVersionGateway().getCurrentVersion(mediaId)
            }

            GlideApp.with(ctx)
                .load(mediaId)
                .placeholder(CoverUtils.getGradient(ctx, mediaId))
                .override(500)
                .priority(Priority.IMMEDIATE)
                .signature(CustomMediaStoreSignature(mediaId, version))
                .into(image)
        }
    }

    protected fun loadOriginalImage(mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

        GlideApp.with(ctx)
            .load(mediaId)
            .placeholder(CoverUtils.getGradient(ctx, mediaId))
            .priority(Priority.IMMEDIATE)
            .override(500)
            .signature(CustomMediaStoreSignature(mediaId, 0))
            .into(image)
    }

    protected fun loadImage(bitmap: Bitmap, mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

        launch {
            val version = withContext(Dispatchers.Default){
                requireContext().getImageVersionGateway().getCurrentVersion(mediaId)
            }

            GlideApp.with(ctx)
                .load(bitmap)
                .placeholder(CoverUtils.getGradient(ctx, mediaId))
                .priority(Priority.IMMEDIATE)
                .override(500)
                .signature(CustomMediaStoreSignature(mediaId, version))
                .into(image)
        }
    }

    protected fun getOriginalImageBitmap(mediaId: MediaId): Bitmap? {
        return GlideApp.with(ctx)
            .asBitmap()
            .load(mediaId)
            .signature(CustomMediaStoreSignature(mediaId, 0))
            .submit()
            .get()
    }

    protected fun showLoader(@StringRes resId: Int) {
        showLoader(getString(resId))
    }

    protected fun showLoader(message: String, dismissable: Boolean = true) {
        progressDialog = ProgressDialog.show(context, "", message, true).apply {
            setCancelable(dismissable)
            setCanceledOnTouchOutside(dismissable)
            setOnCancelListener {
                onLoaderCancelled()
                setOnCancelListener(null)
            }
        }
    }

    protected fun hideLoader() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    protected fun changeImage() {
        MaterialAlertDialogBuilder(ctx)
            .setItems(R.array.edit_item_image_dialog) { _, which ->
                when (which) {
                    0 -> restoreImage()
                    1 -> installOrDo { stylizer ->
                        launch { stylizeImage(stylizer) }
                    }
                }
            }.show()
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        splitManager.registerListener(listener)
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        splitManager.unregisterListener(listener)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        hideLoader()
        sessionId?.let { splitManager.cancelInstall(it) }
    }

    @CallSuper
    override fun dismiss() {
        super.dismiss()
        sessionId?.let { splitManager.cancelInstall(it) }
    }

    protected abstract fun restoreImage()

    private fun installOrDo(action: (Stylizer) -> Unit) {
        val show = splitManager.installedModules.contains(FEATURE_STYLIZE)
        if (!show){
            downloadModuleRequest()
        } else {
            val stylizer = Stylizer.loadClass(requireContext())
            action(stylizer)
        }
    }

    private val listener = SplitInstallStateUpdatedListener { state ->
        Log.v(TAG, "split update listener state, session ${state.sessionId()}, status ${state.status()}")

        when (state.status()){
            SplitInstallSessionStatus.CANCELED -> {
                toggleDownloadModule(false)
                ctx.toast("Stylize module installation cancelled")
                sessionId = null
            }
            SplitInstallSessionStatus.INSTALLED -> {
                toggleDownloadModule(false)
                ctx.toast("Stylize module installed successfully")
                sessionId = null
            }
            SplitInstallSessionStatus.FAILED -> {
                if (state.errorCode() == SplitInstallErrorCode.SERVICE_DIED) {
                    // retry download
                    downloadModuleRequest()
                } else {
                    // display error
                    toggleDownloadModule(false)
                    Log.v(TAG, "split update listener error, code=${state.errorCode()}")
                    ctx.toast("Error code ${state.errorCode()}")
                    sessionId = null
                }
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                splitManager.startConfirmationDialogForResult(
                    state,
                    requireActivity(),
                    FEATURE_STYLIZE_REQUEST_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FEATURE_STYLIZE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            downloadModuleRequest()
        }
    }

    private fun downloadModuleRequest(){
        if (!NetworkUtils.isConnected(requireContext())){
            requireContext().toast(R.string.common_no_internet)
        }
        val request = SplitInstallRequest.newBuilder()
            .addModule(FEATURE_STYLIZE)
            .build()
        toggleDownloadModule(true)
        splitManager.startInstall(request)
            .addOnSuccessListener { sessionId = it }
    }

    protected abstract fun toggleDownloadModule(show: Boolean)

    protected open suspend fun stylizeImage(stylizer: Stylizer){
    }

    abstract fun onLoaderCancelled()

}