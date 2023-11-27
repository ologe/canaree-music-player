package dev.olog.shared.android.extensions

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

inline fun <reified VM : ViewModel> androidx.fragment.app.FragmentActivity.viewModelProvider(

): Lazy<VM> {
        return viewModels()
}

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(

): Lazy<VM> {
        return viewModels()
}

inline fun <reified VM : ViewModel> Fragment.activityViewModelProvider(

) : Lazy<VM> {
        return activityViewModels()
}

inline fun <reified VM : ViewModel> Fragment.parentViewModelProvider(

) : Lazy<VM> {
        return viewModels(ownerProducer = { requireParentFragment() })
}