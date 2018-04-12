package dev.olog.msc.presentation.equalizer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.PopupMenu
import android.view.*
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_preset.*
import kotlinx.android.synthetic.main.fragment_preset.view.*

class PresetFragment : Fragment() {

    companion object {
        fun newInstance(presetName: String): PresetFragment {

            return PresetFragment().withArguments(
                    "preset" to presetName
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_preset, container, false)
        val preset = arguments!!.getString("preset")
        view.presetName.text = preset
        return view
    }

    override fun onResume() {
        super.onResume()
        presetName.setOnClickListener {
            val popupMenu = PopupMenu(act, presetName, Gravity.BOTTOM)
            val fragment = act.supportFragmentManager.findFragmentByTag(EqualizerFragment.TAG)!! as EqualizerFragment
            val presets = fragment.presenter.getPresets()
            val menu = popupMenu.menu
            for (preset in presets) {
                menu.add(Menu.NONE, preset.hashCode(), Menu.NONE, preset)
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val position = presets.indexOfFirst { it.hashCode() == menuItem.itemId }
                act.findViewById<ViewPager>(R.id.pager)?.setCurrentItem(position, true)
                true
            }
            popupMenu.show()
        }
    }

    override fun onPause() {
        super.onPause()
        presetName.setOnClickListener(null)
    }

}
