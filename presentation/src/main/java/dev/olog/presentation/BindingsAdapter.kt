package dev.olog.presentation

import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.images.CoverUtils
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper

object BindingsAdapter {

    private val OVERRIDE_SMALL = 150
    private val OVERRIDE_MID = 600
    private val OVERRIDE_BIG = 600


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
        } else MediaIdHelper.extractCategory(item.mediaId).toInt()

        GlideApp.with(context)
                .load(Uri.parse(item.image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(OVERRIDE_MID)
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
        } else MediaIdHelper.extractCategory(item.mediaId).toInt()

        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(Uri.parse(item.image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(OVERRIDE_BIG)
                .priority(Priority.IMMEDIATE)
                .error(CoverUtils.getGradient(context, id, source))
                .into(view)

    }

}