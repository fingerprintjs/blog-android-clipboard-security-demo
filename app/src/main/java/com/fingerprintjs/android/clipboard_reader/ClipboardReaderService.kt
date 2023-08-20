package com.fingerprintjs.android.clipboard_reader

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter.ClipboardItem
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicBoolean

interface ClipboardReaderService {
    fun pasteClipboardData()
    fun getClipboardHistory(): List<ClipboardItem>
    fun start()
    fun stop()
    fun setOnPasteListener(listener: ((List<ClipboardItem>) -> (Unit))?)
}

class ClipboardReaderForegroundService : Service(), ClipboardReaderService {
    private val binder = LocalBinder()

    private val preferences by lazy {
        ApplicationPreferencesImpl(this.applicationContext)
    }
    private val clipboardUtils by lazy {
        ClipboardUtils(this.applicationContext)
    }

    private val clipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private var lastSavedClip: String? = null

    private val isRunning = AtomicBoolean(false)
    private val executor = Thread()


    private var listener: ((List<ClipboardItem>) -> (Unit))? = null

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun pasteClipboardData() {
        val clipString = clipboardUtils.pasteTheDataSilently(clipboardManager)
        if (clipString == lastSavedClip) {
            return
        }
        val history = getClipboardHistory()
        val newHistory = LinkedList(history)
        newHistory.add(ClipboardItem(System.currentTimeMillis(), clipString))
        preferences.setClipboardHistory(newHistory)
        listener?.invoke(newHistory)
    }

    override fun getClipboardHistory(): List<ClipboardItem> {
        return preferences.getClipboardHistory()
    }

    override fun start() {
        isRunning.set(true)
        executor.run {
            while (isRunning.get()) {
                Thread.sleep(CLIPBOARD_READ_INTERVAL)
                pasteClipboardData()
            }
        }
    }

    override fun stop() {
        isRunning.set(true)
        stopSelf()
    }

    override fun setOnPasteListener(listener: ((List<ClipboardItem>) -> Unit)?) {
        this.listener = listener
    }

    inner class LocalBinder : Binder() {
        fun getService(): ClipboardReaderForegroundService = this@ClipboardReaderForegroundService
    }
}

private const val CLIPBOARD_READ_INTERVAL = 30000L