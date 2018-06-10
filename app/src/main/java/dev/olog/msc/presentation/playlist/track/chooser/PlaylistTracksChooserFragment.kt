package dev.olog.msc.presentation.playlist.track.chooser

import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.library.categories.CategoriesFragment
import dev.olog.msc.presentation.theme.ThemedDialog
import dev.olog.msc.presentation.utils.animation.CircularReveal
import dev.olog.msc.presentation.utils.animation.HasSafeTransition
import dev.olog.msc.presentation.utils.animation.SafeTransition
import dev.olog.msc.presentation.widget.fast.scroller.WaveSideBarView
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_playlist_track_chooser.*
import kotlinx.android.synthetic.main.fragment_playlist_track_chooser.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlaylistTracksChooserFragment : BaseFragment(), HasSafeTransition {

    companion object {
        const val TAG = "PlaylistTracksChooserFragment"
        private const val ARGUMENT_ICON_POS_X = "$TAG.argument.pos.x"
        private const val ARGUMENT_ICON_POS_Y = "$TAG.argument.pos.y"

        @JvmStatic
        fun newInstance(icon: View): PlaylistTracksChooserFragment {
            val x = (icon.x + icon.width / 2).toInt()
            val y = (icon.y + icon.height / 2).toInt()
            return PlaylistTracksChooserFragment().withArguments(
                    ARGUMENT_ICON_POS_X to x,
                    ARGUMENT_ICON_POS_Y to y
            )
        }
    }

    @Inject lateinit var viewModel : PlaylistTracksChooserFragmentViewModel
    @Inject lateinit var adapter: PlaylistTracksChooserFragmentAdapter
    @Inject lateinit var safeTransition: SafeTransition

    private var toast: Toast? = null

    private var errorDisposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            val x = arguments!!.getInt(ARGUMENT_ICON_POS_X)
            val y = arguments!!.getInt(ARGUMENT_ICON_POS_Y)
            safeTransition.execute(this, CircularReveal(ctx, x, y, onAppearFinished = {
                val fragmentManager = activity?.supportFragmentManager

                act.fragmentTransaction {
                    fragmentManager?.findFragmentByTag(CategoriesFragment.TAG)?.let { hide(it) }
                    setReorderingAllowed(true)
                }
            }))
        }
    }

    override fun onDetach() {
        val fragmentManager = activity?.supportFragmentManager
        act.fragmentTransaction {
            fragmentManager!!.findFragmentByTag(CategoriesFragment.TAG)?.let { show(it) }
            setReorderingAllowed(true)
        }
        super.onDetach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        postponeEnterTransition()

        adapter.onFirstEmission {
            startPostponedEnterTransition()
        }

        viewModel.observeSelectedCount()
                .subscribe(this, { size ->
                    val text = when (size){
                        0 -> getString(R.string.playlist_tracks_chooser_no_tracks)
                        else -> resources.getQuantityString(R.plurals.playlist_tracks_chooser_count, size, size)
                    }
                    header.text = text

                    save.toggleVisibility(size > 0, true)
                })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        viewModel.getAllSongs(filter(view))
                .subscribe(this, {
                    adapter.updateDataSet(it)
                    view.sidebar.onDataChanged(it)
                })

        adapter.setAfterDataChanged({
            view.emptyStateText.toggleVisibility(it.isEmpty(), true)
        })

        RxView.clicks(view.back)
                .asLiveData()
                .subscribe(this, { act.onBackPressed() })

        RxView.clicks(view.save)
                .asLiveData()
                .subscribe(this, { showCreateDialog() })

        RxView.clicks(view.filterList)
                .asLiveData()
                .subscribe(this, {
                    view.filterList.toggleSelected()
                    viewModel.toggleShowOnlyFiltered()

                    toast?.cancel()

                    if (view.filterList.isSelected) {
                        toast = act.toast(R.string.playlist_tracks_chooser_show_only_selected)
                    } else {
                        toast = act.toast(R.string.playlist_tracks_chooser_show_all)
                    }

                })

        view.sidebar.scrollableLayoutId = R.layout.item_choose_track
    }

    override fun onResume() {
        super.onResume()
        sidebar.setListener(letterTouchListener)
        clear.setOnClickListener { filter.setText("") }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        clear.setOnClickListener(null)
    }

    private fun showCreateDialog(){
        val builder = ThemedDialog.builder(act)
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
                editTextString.isBlank() -> showError(editTextLayout, R.string.popup_playlist_name_not_valid)
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

    private fun showError(editTextLayout: TextInputLayout, @StringRes errorStringId: Int){
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        editTextLayout.startAnimation(shake)
        editTextLayout.error = getString(errorStringId)
        editTextLayout.isErrorEnabled = true

        errorDisposable.unsubscribe()
        errorDisposable = Single.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ editTextLayout.isErrorEnabled = false }, Throwable::printStackTrace)
    }

    private fun filter(view: View): Observable<String> {
        return RxTextView.afterTextChangeEvents(view.filter)
                .map { it.editable().toString() }
                .filter { it.isBlank() || it.trim().length >= 2 }
                .debounce(250, TimeUnit.MILLISECONDS)
    }

    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
        list.stopScroll()

        val position = when (letter){
            TextUtils.MIDDLE_DOT -> -1
            "#" -> 0
            "?" -> adapter.itemCount - 1
            else -> adapter.indexOf {
                if (it.title.isBlank()) false
                else it.title[0].toUpperCase().toString() == letter
            }
        }
        if (position != -1){
            val layoutManager = list.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun isAnimating(): Boolean = safeTransition.isAnimating

    override fun provideLayoutId(): Int = R.layout.fragment_playlist_track_chooser
}