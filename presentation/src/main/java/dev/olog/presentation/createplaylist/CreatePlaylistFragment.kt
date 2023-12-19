package dev.olog.presentation.createplaylist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.sort.SortType
import dev.olog.presentation.NavigationUtils
import dev.olog.presentation.R
import dev.olog.presentation.base.TextViewDialog
import dev.olog.presentation.base.restoreUpperWidgetsTranslation
import dev.olog.presentation.databinding.FragmentCreatePlaylistBinding
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.utils.hideIme
import dev.olog.shared.widgets.scroller.WaveSideBarView
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.afterTextChange
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.toggleSelected
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.android.extensions.viewLifecycleScope
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePlaylistFragment : Fragment(R.layout.fragment_create_playlist), DrawsOnTop {

    companion object {
        val TAG = CreatePlaylistFragment::class.java.name

        fun newInstance(type: PlaylistType): CreatePlaylistFragment {
            return CreatePlaylistFragment().withArguments(
                 NavigationUtils.ARGUMENTS_PLAYLIST_TYPE to type
            )
        }
    }

    private val binding by viewBinding(FragmentCreatePlaylistBinding::bind) { binding ->
        toast?.cancel()
        binding.list.adapter = null
    }
    private val viewModel by viewModels<CreatePlaylistFragmentViewModel>()
    private val adapter by lazyFast {
        CreatePlaylistFragmentAdapter(viewModel)
    }

    private var toast: Toast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

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
                binding.header.text = text
                binding.fab.toggleVisibility(size > 0, false)
            }

        viewModel.observeData()
            .subscribe(viewLifecycleOwner) {
                adapter.submitList(it)
                restoreUpperWidgetsTranslation()
                binding.emptyStateText.isVisible = it.isEmpty()
            }

        viewModel.observeLetters()
            .subscribe(viewLifecycleOwner) {
                binding.sidebar.onLettersChanged(it)
            }

        viewLifecycleScope.launch {
            binding.editText.afterTextChange()
                .filter { it.isBlank() || it.trim().length >= 2 }
                .debounce(250)
                .collect {
                    viewModel.updateFilter(it)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.sidebar.setListener(letterTouchListener)
        binding.fab.setOnClickListener { showCreateDialog() }
        binding.back.setOnClickListener {
            binding.editText.hideIme()
            act.onBackPressed()
        }
        binding.filterList.setOnClickListener {
            binding.filterList.toggleSelected()
            viewModel.toggleShowOnlyFiltered()

            toast?.cancel()

            if (binding.filterList.isSelected) {
                toast = act.toast(R.string.playlist_tracks_chooser_show_only_selected)
            } else {
                toast = act.toast(R.string.playlist_tracks_chooser_show_all)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.sidebar.setListener(null)
        binding.fab.setOnClickListener(null)
        binding.back.setOnClickListener(null)
        binding.filterList.setOnClickListener(null)
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
        binding.list.stopScroll()

        val position = when (letter) {
            TextUtils.MIDDLE_DOT -> -1
            "#" -> 0
            "?" -> adapter.lastIndex()
            else -> adapter.indexOf { item ->
                val text = item.getText(SortType.TITLE)
                if (text.isBlank()) {
                    return@indexOf false
                }

                return@indexOf text.uppercase() == letter
            }
        }
        if (position != -1) {
            val layoutManager = binding.list.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

}