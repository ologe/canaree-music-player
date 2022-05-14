package dev.olog.feature.playlist.create

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.entity.PlaylistType
import dev.olog.feature.playlist.R
import dev.olog.platform.DrawsOnTop
import dev.olog.platform.fragment.BaseFragment
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.TextUtils
import dev.olog.shared.extension.afterTextChange
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.subscribe
import dev.olog.shared.extension.toast
import dev.olog.shared.extension.toggleSelected
import dev.olog.shared.extension.withArguments
import dev.olog.shared.hideIme
import dev.olog.ui.dialog.TextViewDialog
import dev.olog.ui.model.DisplayableTrack
import dev.olog.ui.scroller.WaveSideBarView
import kotlinx.android.synthetic.main.fragment_create_playlist.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class CreatePlaylistFragment : BaseFragment(), DrawsOnTop {

    companion object {
        val TAG = CreatePlaylistFragment::class.java.name
        val ARGUMENT_PLAYLIST_TYPE = "$TAG.argument.playlist_type"

        @JvmStatic
        fun newInstance(type: PlaylistType): CreatePlaylistFragment {
            return CreatePlaylistFragment().withArguments(
                ARGUMENT_PLAYLIST_TYPE to type
            )
        }
    }

    private val viewModel by viewModels<CreatePlaylistFragmentViewModel>()
    private val adapter by lazyFast {
        CreatePlaylistFragmentAdapter(viewModel = viewModel)
    }

    private var toast: Toast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.setHasFixedSize(true)

        viewModel.observeSelectedCount()
            .subscribe(viewLifecycleOwner) { size ->
                val text = when (size) {
                    0 -> getString(localization.R.string.popup_new_playlist)
                    else -> resources.getQuantityString(
                        localization.R.plurals.playlist_tracks_chooser_count,
                        size,
                        size
                    )
                }
                header.text = text
                fab.isInvisible = (size > 0).not()
            }

        viewModel.observeData()
            .subscribe(viewLifecycleOwner) {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
                sidebar.onDataChanged(it)
                restoreToInitialTranslation()
            }

        sidebar.scrollableLayoutId = R.layout.item_create_playlist

        editText.afterTextChange()
            .filter { it.isBlank() || it.trim().length >= 2 }
            .debounce(250)
            .collectOnViewLifecycle(this) {
                viewModel.updateFilter(it)
            }
    }

    override fun onResume() {
        super.onResume()
        sidebar.setListener(letterTouchListener)
        fab.setOnClickListener { showCreateDialog() }
        back.setOnClickListener {
            editText.hideIme()
            requireActivity().onBackPressed()
        }
        filterList.setOnClickListener {
            filterList.toggleSelected()
            viewModel.toggleShowOnlyFiltered()

            toast?.cancel()

            if (filterList.isSelected) {
                toast = toast(localization.R.string.playlist_tracks_chooser_show_only_selected)
            } else {
                toast = toast(localization.R.string.playlist_tracks_chooser_show_all)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        fab.setOnClickListener(null)
        back.setOnClickListener(null)
        filterList.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toast?.cancel()
        list.adapter = null
    }

    private fun showCreateDialog() {
        TextViewDialog(requireContext(), getString(localization.R.string.popup_new_playlist), null)
            .addTextView(customizeWrapper = {
                hint = getString(localization.R.string.new_playlist_hint)
            })
            .show(
                positiveAction = TextViewDialog.Action(getString(localization.R.string.popup_positive_ok)) {
                    val text = it[0].editableText.toString()
                    if (text.isNotBlank()){
                        viewModel.savePlaylist(text)
                    } else {
                        false
                    }
                }, dismissAction = {
                    dismiss()
                    requireActivity().onBackPressed()
                }
            )
    }

    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
        list.stopScroll()

        val position = when (letter) {
            TextUtils.MIDDLE_DOT -> -1
            "#" -> 0
            "?" -> adapter.lastIndex()
            else -> adapter.indexOf { item ->
                require(item is DisplayableTrack)
                if (item.title.isBlank()) {
                    return@indexOf false
                }

                return@indexOf item.title[0].toUpperCase().toString() == letter
            }
        }
        if (position != -1) {
            val layoutManager = list.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_create_playlist
}