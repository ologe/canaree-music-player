package dev.olog.presentation.playlist.chooser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseActivity
import dev.olog.presentation.playlist.chooser.di.inject
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.activity_playlist_chooser.*
import javax.inject.Inject

class PlaylistChooserActivity : BaseActivity() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var schedulers: Schedulers

    private val viewModel by viewModels<PlaylistChooserActivityViewModel> {
        factory
    }

    private val adapter by lazyFast {
        PlaylistChooserActivityAdapter(this, schedulers)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)

        viewModel.observeData()
            .subscribe(this) { list ->
                if (list.isEmpty()){
                    toast("No playlist found") // TODO localization
                    finish()
                } else {
                    adapter.submitList(list)
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