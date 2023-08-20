package com.fingerprint.security.research.clipboard.clipboard_history_screen


import android.os.Parcelable
import com.fingerprintjs.android.clipboard_reader.ARTICLE_PAGE_URL
import com.fingerprintjs.android.clipboard_reader.ClipboardReaderService
import com.fingerprintjs.android.clipboard_reader.GITHUB_PAGE_URL
import com.fingerprintjs.android.clipboard_reader.clipboard_history_screen.adapter.ClipboardItem
import kotlinx.parcelize.Parcelize


interface ClipboardHistoryPresenter {
    fun update()

    fun attachView(view: ClipboardHistoryView)
    fun detachView()

    fun onSaveState(): Parcelable

    fun attachRouter(router: ClipboardHistoryRouter)
    fun detachRouter()

    fun attachClipboardService(service: ClipboardReaderService)
    fun detachClipboardService()
}


@Parcelize
private class ViewState(
    val clipboardHistory: List<ClipboardItem>?
) : Parcelable


class ClipboardHistoryPresenterImpl(
    state: Parcelable?
) : ClipboardHistoryPresenter {

    private var view: ClipboardHistoryView? = null
    private var router: ClipboardHistoryRouter? = null
    private var service: ClipboardReaderService? = null

    override fun onSaveState(): Parcelable {
        return ViewState(
            service?.getClipboardHistory()
        )
    }

    override fun update() {
        service?.getClipboardHistory()?.let {
            view?.updateClipboardDataset(it)
        }
    }

    override fun attachView(view: ClipboardHistoryView) {
        this.view = view
        subscribeToView()
    }

    private fun subscribeToView() {
        view?.apply {
            setOnArticleButtonClickedListener {
                router?.openLink(ARTICLE_PAGE_URL)
            }
            setOnSourceButtonClickedListener {
                router?.openLink(GITHUB_PAGE_URL)
            }
            setOnRefreshListener {
                router?.refresh()
                stopRefreshing()
            }
            setOnPasteButtonClickedListener {
                service?.pasteClipboardData()
                service?.getClipboardHistory()?.let {
                    updateClipboardDataset(it)
                }
            }
        }
    }

    override fun detachView() {
        view = null
    }

    override fun attachRouter(router: ClipboardHistoryRouter) {
        this.router = router
    }

    override fun detachRouter() {
        this.router = null
    }

    override fun attachClipboardService(service: ClipboardReaderService) {
        this.service = service
    }

    override fun detachClipboardService() {
        this.service = null
    }
}
