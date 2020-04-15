package dev.olog.presentation.playlist.chooser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.app.shortcuts.AppShortcuts
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.activity.BaseActivity
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.activity_playlist_chooser.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PlaylistChooserActivity : BaseActivity() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var schedulers: Schedulers

    @Inject
    lateinit var appShortcuts: AppShortcuts

    private val viewModel by viewModels<PlaylistChooserActivityViewModel> {
        factory
    }

    private val adapter by lazyFast {
        PlaylistChooserActivityAdapter(this, appShortcuts)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)

        viewModel.data
            .onEach { list ->
                if (list.isEmpty()){
                    toast("No playlist found") // TODO localization
                    finish()
                } else {
                    adapter.submitList(list)
                }
            }.launchIn(lifecycleScope)

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