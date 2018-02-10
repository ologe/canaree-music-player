package dev.olog.msc.presentation.neural.network

import android.os.Bundle
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseActivity
import javax.inject.Inject

class NeuralNetworkActivity : BaseActivity() {

    @Inject lateinit var viewModel: NeuralNetworkActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_neural_network)
    }


}