package dev.olog.presentation

import android.databinding.BindingAdapter
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_special_thanks.SpecialThanksModel
import dev.olog.presentation.images.CoverUtils
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import java.io.File

object BindingsAdapter {

    private const val OVERRIDE_SMALL = 150
    private const val OVERRIDE_MID = 400
    private const val OVERRIDE_BIG = 750


    @BindingAdapter("imageSong")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: DisplayableItem) {
        val context = view.context

        val id = MediaIdHelper.extractLeaf(item.mediaId).toInt()

        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(Uri.parse(item.image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(OVERRIDE_SMALL)
                .placeholder(CoverUtils.getGradient(context = context, position = id))
                .into(view)
    }

    @BindingAdapter("imageAlbum")
    @JvmStatic
    fun loadAlbumImage(view: ImageView, item: DisplayableItem) {
        val context = view.context

        GlideApp.with(context).clear(view)

        val source = MediaIdHelper.mapCategoryToSource(item.mediaId)
        val id = if (source == TabViewPagerAdapter.FOLDER){
            MediaIdHelper.extractCategoryValue(item.mediaId).hashCode()
        } else MediaIdHelper.extractCategoryValue(item.mediaId).toInt()

        val image = item.image
        val file = File(image)
        val uri = if (file.exists()){
            Uri.fromFile(file)
        } else {
            Uri.parse(image)
        }

        GlideApp.with(context)
                .load(uri)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(OVERRIDE_MID)
                .priority(Priority.HIGH)
                .error(CoverUtils.getGradient(context = context, position = id, source = source))
                .into(view)
    }

    @BindingAdapter("imageBigAlbum")
    @JvmStatic
    fun loadBigAlbumImage(view: ImageView, item: DisplayableItem) {

        val context = view.context

        val source = MediaIdHelper.mapCategoryToSource(item.mediaId)
        val id = if (source == TabViewPagerAdapter.FOLDER){
            MediaIdHelper.extractCategoryValue(item.mediaId).hashCode()
        } else MediaIdHelper.extractCategoryValue(item.mediaId).toInt()

        GlideApp.with(context).clear(view)

        val image = item.image
        val file = File(image)
        val uri = if (file.exists()){
            Uri.fromFile(file)
        } else {
            Uri.parse(image)
        }

        GlideApp.with(context)
                .load(uri)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(OVERRIDE_BIG)
                .priority(Priority.IMMEDIATE)
                .error(CoverUtils.getGradient(context, id, source))
                .into(view)
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

}