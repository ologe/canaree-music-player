package dev.olog.msc.presentation.neural.network.image.chooser

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseDialogFragment
import dev.olog.msc.presentation.neural.network.NeuralNetworkActivityViewModel
import dev.olog.msc.utils.k.extension.makeDialog
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class NeuralNetworkImageChooser : BaseDialogFragment() {

    companion object {
        const val TAG = "NeuralNetworkImageChoiser"

        fun newInstance(): NeuralNetworkImageChooser {
            return NeuralNetworkImageChooser()
        }
    }

    private lateinit var adapter : NeuralNetworkImageChooserAdapter
    @Inject lateinit var viewModel: NeuralNetworkActivityViewModel
    private var disposable: Disposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.dialog_list, null, false)

        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.neural_pick_image)
                .setView(view)

        val list = view.findViewById<RecyclerView>(R.id.list)

        val dialog = builder.makeDialog()

        adapter = NeuralNetworkImageChooserAdapter(dialog, viewModel)
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 2)

        disposable = viewModel.getImagesAlbum()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::updateData, Throwable::printStackTrace)

        return dialog
    }

    override fun onStop() {
        super.onStop()
        disposable.unsubscribe()
    }

}