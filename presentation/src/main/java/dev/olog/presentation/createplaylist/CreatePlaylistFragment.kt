package dev.olog.presentation.createplaylist

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.CanHandleOnBackPressed
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.utils.hideIme
import dev.olog.presentation.widgets.fascroller.WaveSideBarView
import dev.olog.shared.extensions.*
import dev.olog.shared.utils.TextUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_create_playlist.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CreatePlaylistFragment : BaseFragment(), DrawsOnTop, CanHandleOnBackPressed {

    companion object {
        val TAG = CreatePlaylistFragment::class.java.name
        val ARGUMENT_PLAYLIST_TYPE = "$TAG.argument.playlist_type"

        @JvmStatic
        fun newInstance(type: PlaylistType): CreatePlaylistFragment {
            return CreatePlaylistFragment().withArguments(
                ARGUMENT_PLAYLIST_TYPE to type.ordinal
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<CreatePlaylistFragmentViewModel>(viewModelFactory)
    }
    private val adapter by lazyFast { CreatePlaylistFragmentAdapter(lifecycle, viewModel) }

    private var toast: Toast? = null

    private var errorDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
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
                fab.toggleVisibility(size > 0, true)
            }

        viewModel.observeData()
            .subscribe(viewLifecycleOwner){
                adapter.updateDataSet(it)
                sidebar.onDataChanged(it)
            }

        launch {
            adapter.observeData(false)
                .filter { it.isNotEmpty() }
                .collect { emptyStateText.toggleVisibility(it.isEmpty(), true) }
        }

        sidebar.scrollableLayoutId = R.layout.item_create_playlist

        launch {
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

    private fun showCreateDialog() {
        val builder = AlertDialog.Builder(act)
            .setTitle(R.string.popup_new_playlist)
            .setView(R.layout.layout_edit_text)
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)

        val dialog = builder.show()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)!!
        val clearButton = dialog.findViewById<View>(R.id.clear)!!
        clearButton.setOnClickListener { editText.setText("") }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val editTextString = editText.text.toString()
            when {
                editTextString.isBlank() -> showError(
                    editTextLayout,
                    R.string.popup_playlist_name_not_valid
                )
                else -> {
                    viewModel.savePlaylist(editTextString)
                        .subscribe({}, Throwable::printStackTrace)
                    dialog.dismiss()
                    act.onBackPressed()
                }
            }
        }

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        errorDisposable.unsubscribe()
    }

    private fun showError(editTextLayout: TextInputLayout, @StringRes errorStringId: Int) {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        editTextLayout.startAnimation(shake)
        editTextLayout.error = getString(errorStringId)
        editTextLayout.isErrorEnabled = true

        errorDisposable.unsubscribe()
        errorDisposable = Single.timer(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ editTextLayout.isErrorEnabled = false }, Throwable::printStackTrace)
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

    override fun handleOnBackPressed(): Boolean {
        (act as MainActivity).restoreSlidingPanelHeight()
        return false
    }

    override fun provideLayoutId(): Int = R.layout.fragment_create_playlist
}