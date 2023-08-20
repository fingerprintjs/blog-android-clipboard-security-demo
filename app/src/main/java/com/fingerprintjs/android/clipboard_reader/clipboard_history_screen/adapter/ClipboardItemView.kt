package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.fingerprintjs.android.clipboard_reader.R
import java.util.Date


interface ClipboardItemView {
    fun setTimestamp(timestamp: Long)
    fun setValue(value: String)
    fun setOnDeleteClickListener(listener: () -> (Unit))
}

class ClipboardItemViewImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle),
    ClipboardItemView {

    private val value: TextView by lazy {
        findViewById(R.id.clipboard_item_value)
    }

    private val timestamp: TextView by lazy {
        findViewById(R.id.clipboard_item_timestamp)
    }

    private val deleteBtn: View by lazy {
        findViewById(R.id.delete_btn)
    }

    override fun setTimestamp(timestamp: Long) {
        this.timestamp.text = Date(timestamp).toString()
    }

    override fun setValue(value: String) {
        this.value.text = value
    }

    override fun setOnDeleteClickListener(listener: () -> Unit) {
        deleteBtn.setOnClickListener {
            listener.invoke()
        }
    }
}