package dev.olog.presentation.queue

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.component.CurrentlyPlaying
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.component.onActionDown
import dev.olog.shared.compose.listitem.ListItemTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class PlayingQueueFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val dragListener: IDragListener,
    private val viewModel: PlayingQueueFragmentViewModel
) : ComposeListAdapter<PlayingQueueFragmentItem>(PlayingQueueFragmentItem), TouchableAdapter {

    private val dataFlow = MutableStateFlow<List<PlayingQueueFragmentItem>?>(null)

    fun observeData(): Flow<List<PlayingQueueFragmentItem>> {
        return dataFlow.filterNotNull()
    }

    override fun submitList(list: List<PlayingQueueFragmentItem>) {
        super.submitList(list)
        dataFlow.value = list
    }

    private val moves = mutableListOf<Pair<Int, Int>>()

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: PlayingQueueFragmentItem) {
        // TODO current track index? or some click to scroll to current?
        ListItemTrack(
            mediaId = item.mediaId,
            title = item.title,
            subtitle = item.subtitle,
            onClick = {
                mediaProvider.skipToQueueItem(item.idInPlaylist)
            },
            onLongClick = {
                navigator.toDialog(item.mediaId, viewHolder.itemView)
            },
            leadingContent = {
                // TODO not working correctly
                CurrentlyPlaying(
                    isPlaying = item.isCurrentlyPlaying,
                )
            },
            trailingContent = {
                IconButton(
                    drawableRes = R.drawable.vd_drag_handle,
                    modifier = Modifier.onActionDown {
                        dragListener.onStartDrag(viewHolder)
                    }
                )
            },
        )
    }

    override fun onMoved(from: Int, to: Int) {
        mediaProvider.swap(from, to)
        move(from, to)
        moves.add(from to to)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        mediaProvider.remove(viewHolder.bindingAdapterPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.bindingAdapterPosition
        removeAt(position)
        viewModel.recalculatePositionsAfterRemove(position)
    }

    override fun onClearView() {
        viewModel.recalculatePositionsAfterMove(moves.toList())
        moves.clear()
    }

}