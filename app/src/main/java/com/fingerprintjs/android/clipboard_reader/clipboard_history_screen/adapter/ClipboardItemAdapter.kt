package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fingerprintjs.android.clipboard_reader.R

class ClipboardItemAdapter(
    private val dataset: ArrayList<ClipboardItem>
) : RecyclerView.Adapter<ClipboardItemAdapter.ClipboardItemViewHolder>() {

    private var onItemDeleteListener: ((ClipboardItem) -> (Unit))? = null

    class ClipboardItemViewHolder(val clipboardItemView: ClipboardItemViewImpl) :
        RecyclerView.ViewHolder(clipboardItemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipboardItemViewHolder {
        val clipboardItemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.clipboard_item, parent, false) as ClipboardItemViewImpl

        return ClipboardItemViewHolder(
            clipboardItemView = clipboardItemView
        )
    }

    override fun onBindViewHolder(holder: ClipboardItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.clipboardItemView.setTimestamp(item.unixTimestamp)
        holder.clipboardItemView.setValue(item.value)
        holder.clipboardItemView.setOnDeleteClickListener { onItemDeleteListener?.invoke(item) }
    }

    override fun getItemCount() = dataset.size

    fun setOnDeleteListener(listener: ((ClipboardItem) -> (Unit))) {
        this.onItemDeleteListener = listener
    }
}