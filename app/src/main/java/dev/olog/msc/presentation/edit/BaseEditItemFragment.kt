@file:Suppress("DEPRECATION")

package dev.olog.msc.presentation.edit

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Priority
import dev.olog.msc.Permissions
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.DrawsOnTop
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.theme.ThemedDialog
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.ctx
import dev.olog.msc.utils.k.extension.makeDialog

private const val PICK_IMAGE_CODE = 456

abstract class BaseEditItemFragment : BaseFragment(), DrawsOnTop {

    private var progressDialog: ProgressDialog? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Permissions.requestReadStorage(act)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        hideLoader()
    }

    protected fun setImage(model: DisplayableItem){
        val background = view!!.findViewById<ImageView>(R.id.backgroundCover)
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(background)
        GlideApp.with(ctx).clear(image)

        val img = model.image
        val load: Any = if (ImagesFolderUtils.isChoosedImage(img)){
            img.toUri()
        } else model

        val builder = GlideApp.with(ctx)
                .load(load)
                .placeholder(CoverUtils.getGradient(ctx, model.mediaId))
                .override(500)
                .priority(Priority.IMMEDIATE)

        builder.into(image)
        builder.into(background)
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
        ThemedDialog.builder(ctx)
                .setItems(arrayOf("Pick an image", "Restore default"), { _, which ->
                    if (which == 0){
                        openImagePicker()
                    } else {
                        restoreImage()
                    }
                })
                .makeDialog()
    }

    private fun openImagePicker(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        this.startActivityForResult(Intent.createChooser(intent, getString(R.string.edit_song_change_album_art)),
                dev.olog.msc.presentation.edit.PICK_IMAGE_CODE)
    }

    protected abstract fun restoreImage()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_CODE){
            data?.let { onImagePicked(it.data) }
        }
    }

    protected abstract fun onImagePicked(uri: Uri)

    abstract fun onLoaderCancelled()

}