package com.fingerprint.security.research.clipboard.clipboard_history_screen


import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.fingerprintjs.android.clipboard_reader.ClipboardReaderForegroundService
import com.fingerprintjs.android.clipboard_reader.R


class ClipboardHistoryActivity : AppCompatActivity(), ClipboardHistoryRouter {
    private lateinit var presenter: ClipboardHistoryPresenter

    private lateinit var mService: ClipboardReaderForegroundService
    private var mBound: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clipboard_history)
        init(savedInstanceState)
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
        presenter.detachClipboardService()
        unbindService(connection)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(CLIPBOARD_READER_PRESENTER_STATE_KEY, presenter.onSaveState())
    }

    private fun init(savedInstanceState: Bundle?) {
        val state: Parcelable? =
            savedInstanceState?.getParcelable(CLIPBOARD_READER_PRESENTER_STATE_KEY)
        presenter = ClipboardHistoryPresenterImpl(
            state
        )

    }

    override fun openLink(url: String) {
        openLinkInExternalBrowser(url)
    }

    override fun refresh() {
        presenter.detachView()
        presenter.detachRouter()
        init(null)
        presenter.attachRouter(this)
        presenter.attachView(
            ClipboardHistoryViewImpl(
                this
            )
        )
        bindService()
        presenter.update()
    }

    private fun bindService() {
        if (mBound) {
            presenter.attachClipboardService(mService)
            presenter.update()
            return
        }

        Intent(this, ClipboardReaderForegroundService::class.java).also { intent ->
            if (isMyServiceRunning(ClipboardReaderForegroundService::class.java)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //TODO: build notification
                    applicationContext.startForegroundService(intent)
                } else {
                    applicationContext.startService(intent)
                }
            }
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

    }

    private fun openLinkInExternalBrowser(link: String) {
        val webpage: Uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ClipboardReaderForegroundService.LocalBinder
            mService = binder.getService()
            presenter.attachClipboardService(mService)
            presenter.update()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }
}

private const val CLIPBOARD_READER_PRESENTER_STATE_KEY = "ClipboardReaderPresenterKey"
