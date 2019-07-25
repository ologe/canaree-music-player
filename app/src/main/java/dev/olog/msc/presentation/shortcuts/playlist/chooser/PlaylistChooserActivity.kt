package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.R
import dev.olog.presentation.base.BaseActivity
import dev.olog.shared.android.extensions.asLiveData
import dev.olog.shared.lazyFast
import dev.olog.shared.android.extensions.subscribe
import kotlinx.android.synthetic.main.activity_playlist_chooser.*
import javax.inject.Inject

class PlaylistChooserActivity : BaseActivity() {

    @Inject
    lateinit var presenter: PlaylistChooserActivityViewPresenter
    private val adapter by lazyFast {
        PlaylistChooserActivityAdapter(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)

        presenter.execute(resources)
            .asLiveData()
            .subscribe(this, adapter::updateDataSet)

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