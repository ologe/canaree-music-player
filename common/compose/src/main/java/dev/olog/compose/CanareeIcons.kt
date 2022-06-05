package dev.olog.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Explicit
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

object CanareeIcons {

    val KeyboardArrowRight: ImageVector
        get() = Icons.Rounded.KeyboardArrowRight

    val Search: ImageVector
        get() = Icons.Rounded.Search

    val Close: ImageVector
        get() = Icons.Rounded.Close

    val Bubble: Painter
        @Composable
        get() = painterResource(dev.olog.ui.R.drawable.vd_bubble)

    val MoreVert: ImageVector
        get() = Icons.Rounded.MoreVert

    val Play: ImageVector
        get() = Icons.Rounded.PlayArrow

    val Shuffle: ImageVector
        get() = Icons.Rounded.Shuffle

    val Delete: ImageVector
        get() = Icons.Rounded.Delete

    val PlaylistAdd: ImageVector
        get() = Icons.Rounded.PlaylistAdd

    val Explicit: ImageVector
        get() = Icons.Rounded.Explicit

    val Clear: ImageVector
        get() = Icons.Rounded.Clear

}