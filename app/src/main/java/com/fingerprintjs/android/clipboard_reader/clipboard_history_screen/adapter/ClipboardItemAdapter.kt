package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fingerprintjs.android.clipboard_reader.R

class ClipboardItemAdapter(
    private val dataset: ArrayList<ClipboardItem>
) : RecyclerView.Adapter<ClipboardItemAdapter.ClipboardItemViewHolder>() {

    class ClipboardItemViewHolder(val ClipbpardItemView: ClipboardItemViewImpl) :
        RecyclerView.ViewHolder(ClipbpardItemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipboardItemViewHolder {
        val clipboardItemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.clipboard_item, parent, false) as ClipboardItemViewImpl

        return ClipboardItemViewHolder(
            ClipbpardItemView = clipboardItemView
        )
    }

    override fun onBindViewHolder(holder: ClipboardItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.ClipbpardItemView.setTimestamp(item.unixTimestamp)
        holder.ClipbpardItemView.setValue(item.value)
        holder.ClipbpardItemView.setOnClickListener {
            dataset.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = dataset.size

}