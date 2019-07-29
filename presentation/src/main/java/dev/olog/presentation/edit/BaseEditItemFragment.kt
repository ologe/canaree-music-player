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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.core.MediaId
import dev.olog.core.gateway.getImageVersionGateway
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.CustomMediaStoreSignature
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import dev.olog.shared.android.extensions.ctx

private const val PICK_IMAGE_CODE = 456

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

    protected fun loadImage(any: Any?, mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

        GlideApp.with(ctx)
            .load(any)
            .placeholder(CoverUtils.getGradient(ctx, mediaId))
            .priority(Priority.IMMEDIATE)
            .signature(CustomMediaStoreSignature(mediaId, requireContext().getImageVersionGateway()))
            .into(image)
    }

    protected fun getBitmap(any: Any?, mediaId: MediaId): Bitmap? {
        return GlideApp.with(ctx)
            .asBitmap()
            .load(any)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .signature(CustomMediaStoreSignature(mediaId, requireContext().getImageVersionGateway()))
            .submit(500,500)
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
                    0 -> openImagePicker()
                    1 -> restoreImage()
                    2 -> noImage()
                    3 -> stylizeImage()
                }
            }
            .show()
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        this.startActivityForResult(
            Intent.createChooser(intent, getString(R.string.edit_song_change_album_art)),
            PICK_IMAGE_CODE
        )
    }

    protected abstract fun onImagePicked(uri: Uri)
    protected abstract fun restoreImage()
    protected abstract fun noImage()
    protected open fun stylizeImage(){
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_CODE) {
            data?.data?.let { onImagePicked(it) } ?: Log.w("EditItem", "image not found")
        }
    }

    abstract fun onLoaderCancelled()

}