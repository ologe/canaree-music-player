package dev.olog.shared.components

import android.os.Parcelable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import dev.olog.shared.components.theme.CanareeTheme
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.util.*

@Preview
@Composable
private fun BreadCrumbPreview() {
    CanareeTheme {
        Surface {
            val sep = File.separator
            BreadCrumb(File("/storage${sep}emulated${sep}0${sep}music"))
        }
    }
}

@Composable
fun BreadCrumb(
    file: File,
    separator: String = "¬",
    onClick: (File) -> Unit = {}
) {
    var state by savedInstanceState {
        BreadCrumbState(File(""), "")
    }
    state = state.updateState(file)

    val scrollState = rememberScrollState()
    // TODO scroll
    // TODO if list is scrolled and change to an item with few items, list isn't scrollable anymore

    ScrollableRow(
        scrollState = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .preferredHeight(56.dp)
            .padding(start = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val splits = state.currentFile.path.split(File.separator)
        for ((index, item) in splits.withIndex()) {
            Box(modifier = Modifier.fillMaxHeight()
                .clickable {
                    val path = splits.take(index + 1).joinToString(File.separator)
                    val newFile = File(path)
                    state = state.updateState(newFile)

                    onClick(newFile)
                },
                gravity = ContentGravity.Center
            ) {
                if (item == state.currentSelection) {
                    BreadCrumbText(item)
                } else {
                    MediumEmphasis {
                        BreadCrumbText(item)
                    }
                }
            }
            if (index != splits.lastIndex) {
                Text(separator)
            }
        }
    }
}

@Immutable
@Parcelize
private data class BreadCrumbState (
    val currentFile: File,
    val currentSelection: String
): Parcelable {

    fun updateState(file: File) : BreadCrumbState{
        return when {
            currentFile.path == "" -> BreadCrumbState(file, file.name)
            currentFile.path.startsWith(file.path) -> BreadCrumbState(currentFile, file.name)
            else -> BreadCrumbState(file, file.name)
        }
    }

}

@Composable
private fun BreadCrumbText(title: String) {
    val fixedTitle = if (title.isBlank()) {
        "root"
    } else {
        title
    }
    Text(
        text = fixedTitle.toUpperCase(Locale.ROOT),
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(4.dp)
    )
}