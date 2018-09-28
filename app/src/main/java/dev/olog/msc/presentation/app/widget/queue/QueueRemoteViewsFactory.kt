package dev.olog.msc.presentation.app.widget.queue

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import dev.olog.msc.R
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.playing.queue.GetMiniQueueUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.getBitmapAsync
import javax.inject.Inject

class QueueRemoteViewsFactory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val getMiniQueueUseCase: GetMiniQueueUseCase

) : RemoteViewsService.RemoteViewsFactory {

    private val dataSet = mutableListOf<WidgetItem>()

    override fun onCreate() {
        onDataSetChanged()
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun onDataSetChanged() {
        val data = getMiniQueueUseCase.execute().blockingGet()
        this.dataSet.clear()
        this.dataSet.addAll(data.map { it.toWidgetItem() })
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getItemId(position: Int): Long {
        return -1
    }

    override fun onDestroy() {
    }

    override fun getViewAt(position: Int): RemoteViews {
        val item = dataSet[position]
        val removeViews = RemoteViews(context.packageName, R.layout.widget_colored_with_queue_item)
        removeViews.setTextViewText(R.id.firstText, item.title)
        removeViews.setTextViewText(R.id.secondText, item.subtitle)

        val extras = bundleOf(MusicConstants.EXTRA_SKIP_TO_ITEM_ID to item.idInPlaylist)
        val fillIntent = Intent().also { it.putExtras(extras) }
        removeViews.setOnClickFillInIntent(R.id.root, fillIntent)
        val bitmap = context.getBitmapAsync(DisplayableItem(0, item.mediaId, "", image = item.image), 100)
        removeViews.setImageViewBitmap(R.id.cover, bitmap)

        return removeViews
    }

    override fun getCount(): Int = dataSet.size

    override fun getViewTypeCount(): Int {
        return 1
    }

    private class WidgetItem(
            val id: Long,
            val idInPlaylist: Int,
            val mediaId: MediaId,
            val title: String,
            val subtitle: String,
            val image: String
    )

    private fun Song.toWidgetItem(): WidgetItem {
        return WidgetItem(
                this.id,
                this.trackNumber,
                MediaId.songId(this.id),
                this.title,
                DisplayableItem.adjustArtist(this.artist),
                this.image
        )
    }

}