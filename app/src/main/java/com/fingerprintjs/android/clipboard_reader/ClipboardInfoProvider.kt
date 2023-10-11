package com.fingerprintjs.android.clipboard_reader


import android.content.Context
import android.os.Build
import com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter.ClipboardItem
import java.util.LinkedList


interface ClipboardInfoProvider {
    fun pasteClipboardData()
    fun removeClipboardItem(item: ClipboardItem)
    fun getClipboardHistory(): List<ClipboardItem>
    fun setOnCliboardHistoryChangedListener(listener: ((List<ClipboardItem>) -> (Unit))?)
}

class ClipboardInfoProviderImpl(applicationContext: Context) :
    ClipboardInfoProvider {
    private val preferences by lazy {
        ApplicationPreferencesImpl(applicationContext)
    }
    private val clipboardUtils by lazy {
        ClipboardUtils(applicationContext)
    }

    private var listener: ((List<ClipboardItem>) -> (Unit))? = null

    override fun pasteClipboardData() {
        val clipString = clipboardUtils.pasteAsString()
        val history = getClipboardHistory()

        if (history.isNotEmpty()) {
            val value = history.sortedByDescending { it.unixTimestamp }[0].value
            if (value == clipString) {
                return
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            clipboardUtils.drawOvelay()
        }

        val newHistory = LinkedList(history)
        newHistory.addFirst(ClipboardItem(System.currentTimeMillis(), clipString))
        val historyToStore = newHistory.sortedByDescending { it.unixTimestamp }
        preferences.setClipboardHistory(historyToStore)
        listener?.invoke(historyToStore)
    }

    override fun removeClipboardItem(item: ClipboardItem) {
        val history = getClipboardHistory()
        val newHistory = LinkedList(history.filter {
            it.unixTimestamp != item.unixTimestamp
        })
        preferences.setClipboardHistory(newHistory)
        listener?.invoke(newHistory)
    }

    override fun getClipboardHistory(): List<ClipboardItem> {
        return preferences.getClipboardHistory()
    }

    override fun setOnCliboardHistoryChangedListener(listener: ((List<ClipboardItem>) -> Unit)?) {
        this.listener = listener
    }
}
