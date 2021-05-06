package com.example.glcardrender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aaudin90.glcardrender.api.CardModelLoader
import com.aaudin90.glcardview.GlCardView

class RvAdapter(private val list: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        LayoutInflater.from(parent.context).run {
            when (viewType) {
                0 -> {
                    RenderViewHolder(inflate(R.layout.render_view_holder, null))
                }

                else -> {
                    TextViewHolder(inflate(R.layout.text_view_holder, null))
                }
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val element = list[position]
        if (holder is RenderViewHolder && element is Item.RenderItem) {
            holder.bind(element)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is RenderViewHolder) {
            holder.unbind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    override fun getItemCount(): Int =
        list.size

    sealed class Item(val type: Int) {
        data class RenderItem(
            val data3DProvider: CardModelLoader.Data3DProvider
        ) : Item(0)

        object TextItem : Item(1)
    }

    private class TextViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private class RenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val glCardView: GlCardView = itemView.findViewById(R.id.gl_card_view)

        fun bind(item: Item.RenderItem) {
            glCardView.onStart()
            glCardView.post {
                glCardView.cardSurfaceView.setData3DProvider(item.data3DProvider)
            }
        }

        fun unbind() {
            glCardView.cardSurfaceView.removeRenderer()
            glCardView.onStop()
        }
    }
}