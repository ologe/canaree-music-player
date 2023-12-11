package dev.olog.presentation.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.listitem.ListItemTrack
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.player.widget.PlayerScreen
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.compose.component.onActionDown
import dev.olog.shared.compose.listitem.ListItemFooter
import dev.olog.shared.compose.theme.LocalThemeSettings

internal class PlayerFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: PlayerFragmentViewModel,
    private val presenter: PlayerFragmentPresenter,
    private val dragListener: IDragListener,
    private val playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior
) : ComposeListAdapter<PlayerItem>(PlayerItem), TouchableAdapter {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: PlayerItem) {
        when (item) {
            is PlayerItem.Player -> {
                PlayerScreen(
                    appearance = LocalThemeSettings.current.playerAppearance,
                    viewModel = viewModel,
                    presenter = presenter,
                    navigator = navigator,
                    mediaProvider = mediaProvider,
                    playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
                ) // TODO
            }
            is PlayerItem.Track -> {
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
                    trailingContent = {
                        IconButton(
                            drawableRes = R.drawable.vd_drag_handle,
                            modifier = Modifier.onActionDown {
                                dragListener.onStartDrag(viewHolder)
                            }
                        )
                    }
                )
            }
            is PlayerItem.LoadMore -> {
                ListItemFooter(stringResource(R.string.player_load_more_song))
            }
        }
    }

    override fun onMoved(from: Int, to: Int) {
        val diff = currentList.indexOfFirst { it is PlayerItem.Track }
        mediaProvider.swapRelative(from - diff, to - diff)
        move(from, to)
        notifyItemMoved(from, to)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val diff = currentList.indexOfFirst { it is PlayerItem.Track }
        val realPosition = viewHolder.bindingAdapterPosition - diff
        mediaProvider.removeRelative(realPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.bindingAdapterPosition
        removeAt(position)
        notifyItemRemoved(position)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.bindingAdapterPosition
        val diff = currentList.indexOfFirst { it is PlayerItem.Track }
        mediaProvider.moveRelative(position - diff)
        notifyItemChanged(position)
    }

}