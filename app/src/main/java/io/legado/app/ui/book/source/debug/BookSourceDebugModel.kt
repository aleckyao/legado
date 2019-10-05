package io.legado.app.ui.book.source.debug

import android.app.Application
import io.legado.app.App
import io.legado.app.base.BaseViewModel
import io.legado.app.model.WebBook
import io.legado.app.model.webbook.SourceDebug

class BookSourceDebugModel(application: Application) : BaseViewModel(application),
    SourceDebug.Callback {

    private var webBook: WebBook? = null

    private var callback: ((Int, String)-> Unit)? = null

    fun init(sourceUrl: String?) {
        sourceUrl?.let {
            //优先使用这个，不会抛出异常
            execute {
                val bookSource = App.db.bookSourceDao().getBookSource(sourceUrl)
                bookSource?.let { webBook = WebBook(it) }
            }
        }
    }

    fun observe(callback: (Int, String)-> Unit){
        this.callback = callback
    }

    fun startDebug(key: String, start: (() -> Unit)? = null, error: (() -> Unit)? = null) {
        webBook?.let {
            start?.invoke()
            SourceDebug(it, this).startDebug(key)
        } ?: error?.invoke()
    }

    override fun printLog(state: Int, msg: String) {
        callback?.invoke(state, msg)
    }

    override fun onCleared() {
        super.onCleared()
        SourceDebug.cancelDebug(true)
    }

}