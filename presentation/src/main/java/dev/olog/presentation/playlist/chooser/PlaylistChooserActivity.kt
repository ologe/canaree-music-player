package dev.olog.presentation.playlist.chooser

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseActivity
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.lazyFast

@AndroidEntryPoint
class PlaylistChooserActivity : BaseActivity() {

    private val viewModel by viewModels<PlaylistChooserActivityViewModel>()

    private val adapter by lazyFast { PlaylistChooserActivityAdapter(this) }

    private lateinit var list: RecyclerView
    private lateinit var back: View

    override fun onCreate(savedInstanceState: Bundle?) {
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
                    adapter.submitList(list)
                }
            }

        list.adapter = adapter
        list.layoutManager = GridLayoutManager(this, 3)
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