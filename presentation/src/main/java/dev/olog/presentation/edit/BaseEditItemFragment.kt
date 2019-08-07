@file:Suppress("DEPRECATION")

package dev.olog.presentation.edit

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.bumptech.glide.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.core.MediaId
import dev.olog.core.gateway.getImageVersionGateway
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.CustomMediaStoreSignature
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.model.OriginalImage
import dev.olog.presentation.R
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import dev.olog.shared.android.extensions.ctx

abstract class BaseEditItemFragment : BaseBottomSheetFragment() {

    private var progressDialog: ProgressDialog? = null

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        hideLoader()
    }

    protected fun loadImage(mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

        GlideApp.with(ctx)
            .load(mediaId)
            .placeholder(CoverUtils.getGradient(ctx, mediaId))
            .override(500)
            .priority(Priority.IMMEDIATE)
            .signature(CustomMediaStoreSignature(mediaId, requireContext().getImageVersionGateway()))
            .into(image)
    }

    protected fun loadImage(originalImage: OriginalImage, mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

        GlideApp.with(ctx)
            .load(originalImage)
            .placeholder(CoverUtils.getGradient(ctx, mediaId))
            .priority(Priority.IMMEDIATE)
            .signature(CustomMediaStoreSignature(mediaId, requireContext().getImageVersionGateway()))
            .into(image)
    }

    protected fun loadImage(bitmap: Bitmap, mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

        GlideApp.with(ctx)
            .load(bitmap)
            .placeholder(CoverUtils.getGradient(ctx, mediaId))
            .priority(Priority.IMMEDIATE)
            .signature(CustomMediaStoreSignature(mediaId, requireContext().getImageVersionGateway()))
            .into(image)
    }

    protected fun loadImage(uri: Uri, mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

        GlideApp.with(ctx)
            .load(uri)
            .placeholder(CoverUtils.getGradient(ctx, mediaId))
            .priority(Priority.IMMEDIATE)
            .signature(CustomMediaStoreSignature(mediaId, requireContext().getImageVersionGateway()))
            .into(image)
    }

    protected fun getBitmap(originalImage: OriginalImage, mediaId: MediaId): Bitmap? {
        return GlideApp.with(ctx)
            .asBitmap()
            .load(originalImage)
            .signature(CustomMediaStoreSignature(mediaId, requireContext().getImageVersionGateway()))
            .submit()
            .get()
    }

    protected fun showLoader(@StringRes resId: Int) {
        showLoader(getString(resId))
    }

    protected fun showLoader(message: String) {
        progressDialog = ProgressDialog.show(context, "", message, true).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
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
                    1 -> noImage()
                    2 -> stylizeImage()
                }
            }.show()
    }

    protected abstract fun restoreImage()
    protected abstract fun noImage()
    protected open fun stylizeImage(){
    }

    abstract fun onLoaderCancelled()

}