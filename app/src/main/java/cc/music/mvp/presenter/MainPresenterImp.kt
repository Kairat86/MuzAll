package cc.music.mvp.presenter

import android.util.Log
import cc.music.manager.ApiManager
import cc.music.model.Track
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class MainPresenterImp @Inject constructor(
    private val manager: ApiManager,
    private val disposable: CompositeDisposable,
) : MainPresenter() {
    companion object {
        private val TAG = MainPresenterImp::class.java.simpleName
    }

    private var offset = 0
    private var searching = false
    private lateinit var q: String
    private var loading = false

    init {
        Log.i(TAG, "init")
    }

    override fun onViewAttached() {
        if (results != null) {
            hideLoading()
            view?.show(results)
        } else {
            getPopular(offset)
        }
    }


    private fun search(q: String, offset: Int) {
        disposable += manager.search(q, offset)
            .subscribe(::onContentFetched, ::onError)
    }

    private fun getPopular(offset: Int) {
        Log.i(TAG, "get pop")
        disposable += manager.getPopular(offset)
            .subscribe(::onContentFetched, ::onError)
    }

    private fun onError(t: Throwable) = t.printStackTrace()


    override fun onQueryTextSubmit(q: String) {
        this.q = q
        offset = 0
        showLoading()
        search(q, offset)
        searching = true
    }

    override fun onScrolled() {
        if (!loading) {
            loading = true
            showLoading()
            offset += 25
            if (searching) search(q, offset) else getPopular(offset)
        }
    }

    private fun onContentFetched(response: List<Track>?) {
        if (response?.isEmpty() == true && !searching) {
            hideLoading()
            view?.showServiceUnavailable()
        } else if (view?.trackAdapter == null) {
            view?.show(response?.toMutableList())
        } else {
            hideLoading()
            view?.addAndShow(response)
        }
        loading = false
        results = view?.trackAdapter?.getAll()
    }

    override fun showLoading() {
        view?.showLoading()
    }

    override fun hideLoading() {
        view?.hideLoading()
    }
}
