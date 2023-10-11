package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter

data class ClipboardItem(
    val unixTimestamp: Long,
    val value: String,
)