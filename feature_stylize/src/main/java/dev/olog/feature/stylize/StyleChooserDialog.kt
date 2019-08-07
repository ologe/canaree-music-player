package dev.olog.feature.stylize

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.splitcompat.SplitCompat
import dev.olog.core.entity.ImageStyle
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("unused")
@Keep
class StyleChooserDialog {

    @Keep
    suspend fun create(context: Context) = suspendCoroutine<ImageStyle?> { continuation ->
        SplitCompat.install(context)
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

        var resumed = false

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val style = adapter.selectedStyle
            if (style != null) {
                resumed = true
                continuation.resume(style)
                dialog.dismiss()
            }

        }
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
            resumed = true
            continuation.resume(null)
            dialog.dismiss()
        }
        dialog.setOnDismissListener {
            if (!resumed) {
                continuation.resume(null)
            }
        }
    }

}

@Keep
internal class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val cover: ImageView = view.findViewById(R.id.cover)
    val selectedImage: View = view.findViewById(R.id.selectedImage)

}

@Keep
internal class StyleChooserAdapter : RecyclerView.Adapter<SimpleViewHolder>() {

    var selectedStyle: ImageStyle? = null

    @Keep
    private val data = listOf(
        ImageStyle.BICENTENNIAL_PRINT to R.drawable.ic_bycentennial_print,
        ImageStyle.HEAD_OF_CLOWN to R.drawable.ic_head_of_clown,
        ImageStyle.HORSES_ON_SEASHORE to R.drawable.ic_horses_on_seashore,
        ImageStyle.FEMMES to R.drawable.ic_les_femmes_dalger,
        ImageStyle.POPPY_FIELD to R.drawable.ic_poppy_field,
        ImageStyle.RITMO_PLASTICO to R.drawable.ic_ritmo_plastico,
        ImageStyle.STARRY_NIGHT to R.drawable.ic_starry_night,
        ImageStyle.THE_SCREAM to R.drawable.ic_the_scream,
        ImageStyle.THE_TRAIL to R.drawable.ic_the_trail
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_style, parent, false)

        val viewHolder = SimpleViewHolder(view)

        view.setOnClickListener {
            val indexOldOfStyle = data.indexOfFirst { it.first == selectedStyle }
            selectedStyle = data[viewHolder.adapterPosition].first
            val indexNewOfStyle = data.indexOfFirst { it.first == selectedStyle }
            if (indexOldOfStyle != -1){
                notifyItemChanged(indexOldOfStyle)
            }
            notifyItemChanged(indexNewOfStyle)
        }
        return viewHolder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = data[position]
        Glide.with(context)
            .load(ContextCompat.getDrawable(context, item.second))
            .into(holder.cover)

        val visibility = if (item.first == selectedStyle) View.VISIBLE else View.GONE
        holder.selectedImage.visibility = visibility


    }
}