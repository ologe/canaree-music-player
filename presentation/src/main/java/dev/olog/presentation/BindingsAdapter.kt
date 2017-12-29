package dev.olog.presentation

import android.databinding.BindingAdapter
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.presentation.fragment_special_thanks.SpecialThanksModel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import dev.olog.shared_android.CoverUtils
import java.io.File

object BindingsAdapter {

    const val OVERRIDE_SMALL = 150
    const val OVERRIDE_MID = 300
    const val OVERRIDE_BIG = 500

    private fun loadImageImpl(view: ImageView, item: DisplayableItem,
                              override: Int, priority: Priority = Priority.HIGH, asPlaceholder: Boolean = false){
        val mediaId = item.mediaId
        if (TextUtils.isEmpty(mediaId)){
            return
        }

        val context = view.context

        GlideApp.with(context).clear(view)

        val source = resolveCategory(mediaId)
        val id = resolveId(mediaId)

        var request = GlideApp.with(context)
                .load(resolveUri(item.image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(override)
                .priority(priority)

        if (asPlaceholder){
            request = request.placeholder(CoverUtils.getGradient(context, position = id, source = source))
        } else {
            request = request.error(CoverUtils.getGradient(context, position = id, source = source))
        }

        request.into(view)
    }

    @BindingAdapter("imageSong")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, OVERRIDE_SMALL, asPlaceholder = true)
    }

    @BindingAdapter("imageAlbum")
    @JvmStatic
    fun loadAlbumImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, OVERRIDE_MID, Priority.HIGH)
    }

    @BindingAdapter("imageBigAlbum")
    @JvmStatic
    fun loadBigAlbumImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, OVERRIDE_BIG, Priority.IMMEDIATE)
    }

    @BindingAdapter("imageSpecialThanks")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: SpecialThanksModel) {
        val context = view.context

        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(Uri.EMPTY)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(OVERRIDE_SMALL)
                .placeholder(ContextCompat.getDrawable(view.context, item.image))
                .into(view)
    }

    private fun resolveUri(imageAsString: String): Uri {
        val file = File(imageAsString)
        return if (file.exists()){
            Uri.fromFile(file)
        } else {
            Uri.parse(imageAsString)
        }
    }

    private fun resolveCategory(mediaId: String): Int {
        return MediaIdHelper.mapCategoryToSource(mediaId)
    }

    private fun resolveId(mediaId: String): Int {
        val isLeaf = MediaIdHelper.isLeaf(mediaId)
        if (isLeaf){
            return MediaIdHelper.extractLeaf(mediaId).toInt()
        }

        val category = MediaIdHelper.extractCategory(mediaId)
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)
        if (category == MediaIdHelper.MEDIA_ID_BY_FOLDER){
            return categoryValue.hashCode()
        }
        if (TextUtils.isDigitsOnly(categoryValue)){
            return categoryValue.toInt()
        } else return 0
    }

}