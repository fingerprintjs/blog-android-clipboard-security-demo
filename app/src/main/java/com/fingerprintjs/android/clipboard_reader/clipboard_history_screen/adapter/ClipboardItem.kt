package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClipboardItem(
    val unixTimestamp: Long,
    val value: String,
) : Parcelable