package dev.olog.presentation.player

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder
import dev.olog.presentation.base.adapter.InteractableItem
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.player.ui.PlayerContent
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.component.DragHandle
import dev.olog.shared.compose.list.ListItemSong

internal class PlayerFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: PlayerFragmentViewModel,
    private val dragListener: IDragListener,
) : ComposeListAdapter<PlayerItem>(), TouchableAdapter {

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is PlayerItem.Player) {
            return R.id.player_view_type
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeViewHolder {
        if (viewType == R.id.player_view_type) {
            return PlayerViewHolder(ComposeView(parent.context))
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun bind(holder: ComposeViewHolder, item: PlayerItem, position: Int) {
        holder.setContent {
            when (item) {
                is PlayerItem.Player -> PlayerContent(
                    itemView = holder.itemView,
                    navigator = navigator,
                    viewModel = viewModel,
                )
                is PlayerItem.Song -> ListItemSong(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    trailingContent = { DragHandle { dragListener.onStartDrag(holder) } },
                    onClick = { mediaProvider.skipToQueueItem(item.idInPlaylist.toInt()) },
                    onLongClick = { navigator.toDialog(item.mediaId, holder.itemView) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                PlayerItem.LoadMore -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.player_load_more_song),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Theme.textColorPrimary,
                            letterSpacing = 0.01.em
                        )
                    }
                }
            }
        }
    }

    override fun onMoved(from: Int, to: Int) {
        val realFrom = from - 1
        val realTo = to - 1
        mediaProvider.swapRelative(realFrom, realTo)
        moveItem(from, to)
    }

    override fun onClearView() {
        commitChange {
            // no op, works out of the box
        }
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val realPosition = viewHolder.adapterPosition - 1
        mediaProvider.removeRelative(realPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        removeItem(viewHolder.adapterPosition)
        commitChange {
            // no op, works out of the box
        }
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val realPosition = viewHolder.adapterPosition - 1
        mediaProvider.moveRelative(realPosition)
        notifyItemChanged(viewHolder.adapterPosition)
    }

}

@Stable
sealed interface PlayerItem {

    @Stable
    object Player : PlayerItem

    @Stable
    data class Song(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val idInPlaylist: Long,
    ) : PlayerItem, InteractableItem

    @Stable
    object LoadMore : PlayerItem

}

class PlayerViewHolder(view: View): ComposeViewHolder(view)