@file:Suppress("DEPRECATION")

package dev.olog.msc.presentation.edit

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Priority
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.msc.R
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import dev.olog.shared.Permissions
import dev.olog.shared.extensions.act
import dev.olog.shared.extensions.ctx

private const val PICK_IMAGE_CODE = 456

abstract class BaseEditItemFragment : BaseBottomSheetFragment() {

    private var progressDialog: ProgressDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Permissions.requestReadStorage(act)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        hideLoader()
    }

    protected fun setImage(mediaId: MediaId){
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(image)

//        val img = model.image
//        val load: Any = if (ImagesFolderUtils.isChoosedImage(img)){
//            img.toUri()
//        } else model TODO

        GlideApp.with(ctx)
                .load(mediaId)
                .placeholder(CoverUtils.getGradient(ctx, mediaId))
                .override(500)
                .priority(Priority.IMMEDIATE)
                .into(image)
    }


    protected fun showLoader(@StringRes resId: Int){
        showLoader(getString(resId))
    }

    protected fun showLoader(message: String){
        progressDialog = ProgressDialog.show(context, "", message, true)
        progressDialog?.setCancelable(true)
        progressDialog?.setCanceledOnTouchOutside(true)
        progressDialog?.setOnCancelListener {
            onLoaderCancelled()
            progressDialog?.setOnCancelListener(null)
        }
    }

    protected fun hideLoader(){
        progressDialog?.dismiss()
        progressDialog = null
    }

    protected fun changeImage(){
        AlertDialog.Builder(ctx)
                .setItems(R.array.edit_item_image_dialog) { _, which ->
                    when (which){
                        0 -> openImagePicker()
                        1 -> restoreImage()
                        2 -> noImage()
                    }
                }
                .show()
    }

    private fun openImagePicker(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        this.startActivityForResult(Intent.createChooser(intent, getString(R.string.edit_song_change_album_art)), PICK_IMAGE_CODE)
    }

    protected abstract fun restoreImage()

    protected abstract fun noImage()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_CODE){
            data?.data?.let { onImagePicked(it) } ?: Log.w("EditItem", "image not found")
        }
    }

    protected abstract fun onImagePicked(uri: Uri)

    abstract fun onLoaderCancelled()

}