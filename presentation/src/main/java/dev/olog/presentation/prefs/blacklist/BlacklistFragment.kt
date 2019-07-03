package dev.olog.presentation.prefs.blacklist

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseDialogFragment
import dev.olog.shared.extensions.ctx
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.extensions.toast
import javax.inject.Inject

class BlacklistFragment : BaseDialogFragment() {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    @Inject lateinit var presenter: BlacklistFragmentPresenter
    private val adapter by lazyFast { BlacklistFragmentAdapter(lifecycle) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.fragment_list, null, false)

        val builder = AlertDialog.Builder(ctx)
                .setTitle(R.string.prefs_blacklist_title)
                .setMessage(R.string.prefs_blacklist_description)
                .setView(view)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, null)

        val list = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list)
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 3)

        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val allIsBlacklisted = adapter.getData().all { it.isBlacklisted }
                    if (allIsBlacklisted){
                        showErrorMessage()
                    } else {
                        presenter.saveBlacklisted(adapter.getData())
                        notifyMediaStore()
                        dismiss()
                    }
                }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.observeData()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)
    }

    private fun notifyMediaStore(){
        val contentResolver = context!!.contentResolver
        contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
    }

    private fun showErrorMessage(){
        activity!!.toast(R.string.prefs_blacklist_error)
    }

}