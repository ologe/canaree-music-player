package dev.olog.msc.presentation

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import dev.olog.msc.app.GlideApp
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.glide.AudioFileCover
import dev.olog.msc.presentation.library.folder.tree.DisplayableFile
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong
import dev.olog.msc.presentation.special.thanks.SpecialThanksModel
import dev.olog.msc.presentation.utils.images.RippleTarget
import dev.olog.msc.presentation.widget.QuickActionView
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.img.ImagesFolderUtils

object BindingsAdapter {

    private const val OVERRIDE_SMALL = 150
    private const val OVERRIDE_MID = 400

    @JvmStatic
    @BindingAdapter("fileTrackLoader")
    fun loadFile(view: ImageView, item: DisplayableFile){
        val context = view.context
        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(AudioFileCover(item.path!!))
                .override(OVERRIDE_SMALL)
                .placeholder(CoverUtils.getGradient(context, MediaId.songId(item.path.hashCode().toLong())))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }

    @JvmStatic
    @BindingAdapter("fileDirLoader")
    fun loadDirImage(view: ImageView, item: DisplayableFile){
        val path = item.path ?: ""
        val displayableItem = DisplayableItem(0, MediaId.folderId(path),
                "", "", ImagesFolderUtils.forFolder(view.context, path))
        loadImageImpl(view, displayableItem, OVERRIDE_SMALL)
    }

    @JvmStatic
    private fun loadImageImpl(
            view: ImageView,
            item: DisplayableItem,
            override: Int,
            priority: Priority = Priority.HIGH,
            crossfade: Boolean = true){

        val mediaId = item.mediaId
        val context = view.context

        GlideApp.with(context).clear(view)

        val load: Any = if (ImagesFolderUtils.isChoosedImage(item.image)){
            item.image
        } else item

        var builder = GlideApp.with(context)
                .load(load)
                .override(override)
                .priority(priority)
                .placeholder(CoverUtils.getGradient(context, mediaId))
        if (crossfade){
            builder = builder.transition(DrawableTransitionOptions.withCrossFade())
        }
        builder.into(RippleTarget(view, mediaId.isLeaf))
    }

    @BindingAdapter("imageSong")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, OVERRIDE_SMALL)
    }

    @BindingAdapter("imageSong")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: DisplayableQueueSong) {
        loadImageImpl(view,
                DisplayableItem(item.type, item.mediaId, "", "", item.image), OVERRIDE_SMALL)
    }

    @BindingAdapter("imageAlbum")
    @JvmStatic
    fun loadAlbumImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, OVERRIDE_MID, Priority.HIGH)
    }

    @BindingAdapter("imageBigAlbum")
    @JvmStatic
    fun loadBigAlbumImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, item, if (AppConstants.useFakeData) 1000 else Target.SIZE_ORIGINAL, Priority.IMMEDIATE, crossfade = false)
    }

    @BindingAdapter("imageSpecialThanks")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: SpecialThanksModel) {
        GlideApp.with(view)
                .load(ContextCompat.getDrawable(view.context, item.image))
                .into(view)
    }

    @BindingAdapter("setBoldIfTrue")
    @JvmStatic
    fun setBoldIfTrue(view: TextView, setBold: Boolean){
        val style = if (setBold) Typeface.BOLD else Typeface.NORMAL
        view.setTypeface(null, style)
    }

    @BindingAdapter("quickActionItem")
    @JvmStatic
    fun quickActionItem(view: QuickActionView, item: DisplayableItem){
        view.setId(item.mediaId)
    }

}