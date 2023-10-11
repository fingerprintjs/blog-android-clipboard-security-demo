package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fingerprintjs.android.clipboard_reader.ClipboardInfoProviderImpl
import com.fingerprintjs.android.clipboard_reader.R


class ClipboardHistoryActivity : AppCompatActivity(), ClipboardHistoryRouter {
    private lateinit var presenter: ClipboardHistoryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clipboard_history)
        init()
        presenter.attachRouter(this)
        presenter.attachView(
            ClipboardHistoryViewImpl(
                this
            )
        )
    }

    override fun onDestroy() {
        presenter.detachView()
        presenter.detachRouter()
        super.onDestroy()
    }

    private fun init() {
        presenter = ClipboardHistoryPresenterImpl(
            ClipboardInfoProviderImpl(this.applicationContext)
        )

    }

    override fun openLink(url: String) {
        openLinkInExternalBrowser(url)
    }

    override fun refresh() {
        presenter.detachView()
        presenter.detachRouter()
        presenter.attachRouter(this)
        presenter.attachView(
            ClipboardHistoryViewImpl(
                this
            )
        )
        presenter.update()
    }

    private fun openLinkInExternalBrowser(link: String) {
        val webpage: Uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}