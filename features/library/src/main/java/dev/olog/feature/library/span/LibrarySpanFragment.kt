package dev.olog.feature.library.span

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.MediaIdCategory
import dev.olog.feature.presentation.base.extensions.argument
import dev.olog.navigation.Params
import dev.olog.shared.components.SingleChoiceList
import dev.olog.shared.components.theme.CanareeTheme

@AndroidEntryPoint
class LibrarySpanFragment : BottomSheetDialogFragment() {

    private val category by argument<MediaIdCategory>(Params.CATEGORY)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                CanareeTheme {
                    val viewModel = viewModel<LibrarySpanFragmentViewModel>()
                    val spanCount = viewModel.getSpanCount(category)
                    LibrarySpanFragmentContent(spanCount) {
                        viewModel.setSpanCount(category, it)
                        dismiss()
                    }
                }
            }
        }
    }

}

@Preview
@Composable
private fun LibrarySpanFragmentContentPreview() {
    CanareeTheme {
        LibrarySpanFragmentContent(selected = 1)
    }
}

@Composable
private fun LibrarySpanFragmentContent(
    selected: Int,
    dismiss: (Int) -> Unit = {}
) {
    val items = (1..4).map { it }
    SingleChoiceList(
        items = items,
        selected = selected,
        onClick = dismiss
    )
}