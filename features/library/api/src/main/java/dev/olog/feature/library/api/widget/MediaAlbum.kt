package dev.olog.feature.library.api.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.compose.Background
import dev.olog.compose.ThemePreviews
import dev.olog.compose.WithMediumEmphasys
import dev.olog.compose.gesture.scalableContent
import dev.olog.compose.glide.RemoteImage
import dev.olog.compose.shape.LocalImageShape
import dev.olog.compose.shape.LocalQuickAction
import dev.olog.compose.shape.toComposeShape
import dev.olog.compose.theme.CanareeTheme
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.platform.theme.ImageShape
import dev.olog.platform.theme.QuickAction

@Composable
fun MediaAlbum(
    mediaId: MediaId,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    shape: Shape = if (mediaId.isAnyArtist) CircleShape else LocalImageShape.current.toComposeShape(),
) {
    Column(
        modifier = modifier
            .scalableContent(.95f)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            RemoteImage(
                mediaId = mediaId,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(shape)
            )

            QuickAction(
                mediaId = mediaId,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            maxLines = 1,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
        )
        subtitle?.let {
            Spacer(modifier = Modifier.height(2.dp))

            WithMediumEmphasys {
                Text(
                    text = it,
                    maxLines = 1,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Background {
            LazyVerticalGrid(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Adaptive(100.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                for ((i, shape) in ImageShape.values().withIndex()) {
                    for ((j, action) in QuickAction.values().withIndex()) {
                        item {
                            CompositionLocalProvider(LocalQuickAction provides action) {
                                MediaAlbum(
                                    mediaId = MediaId.songId((i + j).toLong()),
                                    title = "${shape}, ${action}",
                                    subtitle = "Subtitle",
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = shape.toComposeShape(),
                                )
                            }
                        }
                    }
                }
                item {
                    MediaAlbum(
                        mediaId = MediaId.songId(4),
                        title = "No subtitle",
                        modifier = Modifier.fillMaxWidth(),
                        subtitle = null,
                    )
                }

                item {
                    MediaAlbum(
                        mediaId = MediaId.createCategoryValue(
                            MediaIdCategory.ARTISTS, "1",
                        ),
                        title = "Artist",
                        modifier = Modifier.fillMaxWidth(),
                        subtitle = null,
                    )
                }
            }
        }
    }
}