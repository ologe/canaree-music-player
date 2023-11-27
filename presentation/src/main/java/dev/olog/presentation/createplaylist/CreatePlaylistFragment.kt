package dev.olog.presentation.createplaylist

import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.NavigationUtils
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.TextViewDialog
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.utils.hideIme
import dev.olog.presentation.widgets.fascroller.WaveSideBarView
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_create_playlist.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePlaylistFragment : BaseFragment(), DrawsOnTop {

    companion object {
        val TAG = CreatePlaylistFragment::class.java.name

        @JvmStatic
        fun newInstance(type: PlaylistType): CreatePlaylistFragment {
            return CreatePlaylistFragment().withArguments(
                 NavigationUtils.ARGUMENTS_PLAYLIST_TYPE to type.ordinal
            )
        }
    }

    private val viewModel by viewModels<CreatePlaylistFragmentViewModel>()
    private val adapter by lazyFast {
        CreatePlaylistFragmentAdapter(
            lifecycle,
            viewModel
        )
    }

    private var toast: Toast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.setHasFixedSize(true)

        viewModel.observeSelectedCount()
            .subscribe(viewLifecycleOwner) { size ->
                val text = when (size) {
                    0 -> getString(R.string.popup_new_playlist)
                    else -> resources.getQuantityString(
                        R.plurals.playlist_tracks_chooser_count,
                        size,
                        size
                    )
                }
                header.text = text
                fab.toggleVisibility(size > 0, false)
            }

        viewModel.observeData()
            .subscribe(viewLifecycleOwner) {
                adapter.updateDataSet(it)
                sidebar.onDataChanged(it)
                restoreUpperWidgetsTranslation()
            }

        viewLifecycleScope.launch {
            adapter.observeData(false)
                .filter { it.isNotEmpty() }
                .collect { emptyStateText.toggleVisibility(it.isEmpty(), true) }
        }

        sidebar.scrollableLayoutId = R.layout.item_create_playlist

        viewLifecycleScope.launch {
            editText.afterTextChange()
                .filter { it.isBlank() || it.trim().length >= 2 }
                .debounce(250)
                .collect {
                    viewModel.updateFilter(it)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        sidebar.setListener(letterTouchListener)
        fab.setOnClickListener { showCreateDialog() }
        back.setOnClickListener {
            editText.hideIme()
            act.onBackPressed()
        }
        filterList.setOnClickListener {
            filterList.toggleSelected()
            viewModel.toggleShowOnlyFiltered()

            toast?.cancel()

            if (filterList.isSelected) {
                toast = act.toast(R.string.playlist_tracks_chooser_show_only_selected)
            } else {
                toast = act.toast(R.string.playlist_tracks_chooser_show_all)
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
        TextViewDialog(act, getString(R.string.popup_new_playlist), null)
            .addTextView(customizeWrapper = {
                hint = getString(R.string.new_playlist_hint)
            })
            .show(
                positiveAction = TextViewDialog.Action(getString(R.string.popup_positive_ok)) {
                    val text = it[0].editableText.toString()
                    if (text.isNotBlank()){
                        viewModel.savePlaylist(text)
                    } else {
                        false
                    }
                }, dismissAction = {
                    dismiss()
                    act.onBackPressed()
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