package dev.olog.presentation.playlist.chooser

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseActivity
import dev.olog.presentation.playlist.chooser.di.inject
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.viewModelProvider
import dev.olog.shared.lazyFast
import javax.inject.Inject

class PlaylistChooserActivity : BaseActivity() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazyFast { viewModelProvider<PlaylistChooserActivityViewModel>(factory) }

    private val adapter by lazyFast { PlaylistChooserActivityAdapter(this) }

    private lateinit var list: RecyclerView
    private lateinit var back: View

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)
        list = findViewById(R.id.list)
        back = findViewById(R.id.back)

        viewModel.observeData()
            .subscribe(this) { list ->
                if (list.isEmpty()){
                    toast("No playlist found")
                    finish()
                } else {
                    adapter.updateDataSet(list)
                }
            }

        list.adapter = adapter
        list.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}