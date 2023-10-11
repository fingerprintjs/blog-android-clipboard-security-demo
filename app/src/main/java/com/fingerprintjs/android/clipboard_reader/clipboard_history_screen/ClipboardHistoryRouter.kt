package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen


interface ClipboardHistoryRouter {
    fun openLink(url: String)
    fun refresh()
}