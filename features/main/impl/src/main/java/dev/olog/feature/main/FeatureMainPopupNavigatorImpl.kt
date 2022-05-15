package dev.olog.feature.main

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.feature.main.popup.PopupMenuFactory
import dev.olog.platform.navigation.allowed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeatureMainPopupNavigatorImpl @Inject constructor(
    private val activity: FragmentActivity,
    private val popupFactory: Lazy<PopupMenuFactory>,
) : FeatureMainPopupNavigator {

    override fun toItemDialog(
        anchor: View,
        mediaId: MediaId
    ) {
        if (allowed()) {
            activity.lifecycleScope.launch {
                val popup = popupFactory.get().create(anchor, mediaId)
                withContext(Dispatchers.Main) {
                    popup.show()
                }
            }
        }
    }

}