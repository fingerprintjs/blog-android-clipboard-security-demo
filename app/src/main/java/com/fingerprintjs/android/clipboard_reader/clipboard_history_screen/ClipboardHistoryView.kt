package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen


import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fingerprintjs.android.clipboard_reader.R
import com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter.ClipboardItem
import com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter.ClipboardItemAdapter


interface ClipboardHistoryView {
    fun setOnSourceButtonClickedListener(listener: () -> (Unit))
    fun setOnArticleButtonClickedListener(listener: () -> (Unit))

    fun setOnRefreshListener(listener: () -> Unit)
    fun stopRefreshing()

    fun setOnPasteButtonClickedListener(listener: () -> Unit)

    fun setOnItemRemovedListener(listener: (ClipboardItem) -> Unit)
    fun updateClipboardDataset(dataset: List<ClipboardItem>)
}

class ClipboardHistoryViewImpl(
    private val activity: ClipboardHistoryActivity
) : ClipboardHistoryView {
    private val swipeRefreshLayout =
        activity.findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh)

    private val recycler = activity.findViewById<RecyclerView>(R.id.clipboard_history_recycler)
    private val viewManager = LinearLayoutManager(activity)
    private val dataset = ArrayList<ClipboardItem>()
    private val adapter = ClipboardItemAdapter(dataset)

    private val pasteBtn = activity.findViewById<View>(R.id.paste_btn)
    private val sourceButton = activity.findViewById<View>(R.id.github_button)
    private val articleButton = activity.findViewById<View>(R.id.read_more_button)

    init {
        recycler.layoutManager = viewManager
        recycler.adapter = adapter
    }

        override fun setOnSourceButtonClickedListener(listener: () -> Unit) {
        sourceButton.setOnClickListener {
            listener.invoke()
        }
    }

    override fun setOnArticleButtonClickedListener(listener: () -> Unit) {
        articleButton.setOnClickListener {
            listener.invoke()
        }
    }

    override fun setOnRefreshListener(listener: () -> Unit) {
        swipeRefreshLayout.setOnRefreshListener {
            listener.invoke()
        }
    }

    override fun stopRefreshing() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun setOnPasteButtonClickedListener(listener: () -> Unit) {
        pasteBtn.setOnClickListener {
            listener.invoke()
        }
    }

    override fun setOnItemRemovedListener(listener: (ClipboardItem) -> Unit) {
        (recycler.adapter as ClipboardItemAdapter).setOnDeleteListener {
            listener.invoke(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateClipboardDataset(dataset: List<ClipboardItem>) {
        activity.runOnUiThread {
            this.dataset.clear()
            dataset.forEach {
                this.dataset.add(it)
            }
            adapter.notifyDataSetChanged()
        }
    }
}
