package dev.olog.presentation.fragment_detail

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.withArguments
import kotlinx.android.synthetic.main.fragment_detail.view.*
import javax.inject.Inject

class DetailFragment : BaseFragment() {

    companion object {
        const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )

        }
    }

    @Inject lateinit var viewModel: DetailFragmentViewModel
//    @Inject lateinit var horizontalAdapter: DetailHorizontalAdapter
    @Inject lateinit var adapter: DetailAdapter

    private lateinit var layoutManager: GridLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        viewModel.siblingsObservable
//                .asLiveData()
//                .subscribe(this, horizontalAdapter::updateDataSet)

//        viewModel.songListLiveData
//                .subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context!!, 2, GridLayoutManager.VERTICAL, false)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if (adapter.getItem(position).type == R.layout.item_detail_album) 1 else 2
            }
        }
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_detail, container, false)
}