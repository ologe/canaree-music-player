package dev.olog.msc.presentation.preferences.blacklist

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseDialogFragment
import dev.olog.msc.presentation.theme.ThemedDialog
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.ctx
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.toast
import javax.inject.Inject

class BlacklistFragment : BaseDialogFragment() {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    @Inject lateinit var presenter: BlacklistFragmentPresenter
    private lateinit var adapter : BlacklistFragmentAdapter


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.data.asLiveData()
                .subscribe(this, adapter::updateDataSet)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.dialog_list, null, false)

        val builder = ThemedDialog.builder(ctx)
                .setTitle(R.string.prefs_blacklist_title)
                .setMessage(R.string.prefs_blacklist_description)
                .setView(view)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, null)

        val list = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list)
        adapter = BlacklistFragmentAdapter()
        list.adapter = adapter
        list.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)

        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val allIsBlacklisted = adapter.data.all { it.isBlacklisted }
                    if (allIsBlacklisted){
                        showErrorMessage()
                    } else {
                        presenter.setDataSet(adapter.data)
                        notifyMediaStore()
                        dismiss()
                    }
                }

        return dialog
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