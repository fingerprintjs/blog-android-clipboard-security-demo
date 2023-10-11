package com.fingerprintjs.android.clipboard_reader.clipboard_history_screen


import com.fingerprintjs.android.clipboard_reader.ARTICLE_PAGE_URL
import com.fingerprintjs.android.clipboard_reader.ClipboardInfoProvider
import com.fingerprintjs.android.clipboard_reader.GITHUB_PAGE_URL


interface ClipboardHistoryPresenter {
    fun update()

    fun attachView(view: ClipboardHistoryView)
    fun detachView()

    fun attachRouter(router: ClipboardHistoryRouter)
    fun detachRouter()
}

class ClipboardHistoryPresenterImpl(
    private val clipboardInfoProvider: ClipboardInfoProvider
) : ClipboardHistoryPresenter {

    private var view: ClipboardHistoryView? = null
    private var router: ClipboardHistoryRouter? = null

    override fun update() {
        view?.updateClipboardDataset(clipboardInfoProvider.getClipboardHistory())
    }

    override fun attachView(view: ClipboardHistoryView) {
        this.view = view
        subscribeToView()
    }

    private fun subscribeToView() {
        view?.apply {
            updateClipboardDataset(clipboardInfoProvider.getClipboardHistory())
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
            setOnItemRemovedListener {
                clipboardInfoProvider.removeClipboardItem(it)
            }
            setOnPasteButtonClickedListener {
                clipboardInfoProvider.pasteClipboardData()
            }
            clipboardInfoProvider.setOnCliboardHistoryChangedListener {
                updateClipboardDataset(it)
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
}
