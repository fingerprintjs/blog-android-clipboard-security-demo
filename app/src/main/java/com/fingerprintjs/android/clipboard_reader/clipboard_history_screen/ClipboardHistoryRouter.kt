package com.fingerprint.security.research.clipboard.clipboard_history_screen


interface ClipboardHistoryRouter {
    fun openLink(url: String)
    fun refresh()
    //TODO: implement and handle the permission request
}