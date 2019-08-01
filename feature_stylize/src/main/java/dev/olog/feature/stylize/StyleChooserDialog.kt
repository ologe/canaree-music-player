package dev.olog.feature.stylize

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.core.entity.ImageStyle
import kotlinx.android.synthetic.main.item_style.view.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("unused")
@Keep
class StyleChooserDialog {

    @Keep
    suspend fun create(context: Context) = suspendCoroutine<ImageStyle?> { continuation ->
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.fragment_style, null, false)

        val builder = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setTitle("Choose a style")
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)

        val list = view.findViewById<RecyclerView>(R.id.list)

        val adapter = StyleChooserAdapter()

        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 4)

        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val style = adapter.selectedStyle
            if (style != null) {
                continuation.resume(style)
                dialog.dismiss()
            }

        }
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
            continuation.resume(null)
            dialog.dismiss()
        }
    }

}

internal class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view)

internal class StyleChooserAdapter : RecyclerView.Adapter<SimpleViewHolder>() {

    var selectedStyle: ImageStyle? = null

    private val data = listOf(
        ImageStyle.BICENTENNIAL_PRINT to R.drawable.bycentennial_print,
        ImageStyle.HEAD_OF_CLOWN to R.drawable.head_of_clown,
        ImageStyle.HORSES_ON_SEASHORE to R.drawable.horses_on_seashore,
        ImageStyle.FEMMES to R.drawable.les_femmes_dalger,
        ImageStyle.POPPY_FIELD to R.drawable.poppy_field,
        ImageStyle.RITMO_PLASTICO to R.drawable.ritmo_plastico,
        ImageStyle.STARRY_NIGHT to R.drawable.starry_night,
        ImageStyle.THE_SCREAM to R.drawable.the_scream,
        ImageStyle.THE_TRAIL to R.drawable.the_trail
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_style, parent, false)

        val viewHolder = SimpleViewHolder(view)

        view.setOnClickListener {
            selectedStyle = data[viewHolder.adapterPosition].first
            notifyDataSetChanged()
        }
        return viewHolder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = data[position]
        Glide.with(context)
            .load(ContextCompat.getDrawable(context, item.second))
            .into(holder.itemView.cover)

        val visibility = if (item.first == selectedStyle) View.VISIBLE else View.GONE
        holder.itemView.selectedImage.visibility = visibility


    }
}