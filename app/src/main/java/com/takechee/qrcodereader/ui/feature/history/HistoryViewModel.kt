package com.takechee.qrcodereader.ui.feature.history

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.takechee.qrcodereader.result.Event
import com.takechee.qrcodereader.result.fireEvent
import com.takechee.qrcodereader.ui.common.base.BaseViewModel
import javax.inject.Inject

class HistoryViewModel @Inject constructor(

) : BaseViewModel(), LifecycleObserver, HistoryEventListener {

    private val _navigateTo = MutableLiveData<Event<NavDirections>>()
    val navigateTo: LiveData<Event<NavDirections>> = _navigateTo.distinctUntilChanged()

    val urls: LiveData<List<String>> = MutableLiveData(
        listOf(
            "https://takechee.com/takeblo/",
            "https://s10i.me/whitenote/",
            "https://qiita.com/ru_ri21/items/2fdcef6f522f61f1545e"
        )
    )

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
//        fireOpenReaderEvent(ifNeeded = true)
    }

    override fun onHistoryItemClick(url: String) {
        _navigateTo.fireEvent { HistoryFragmentDirections.toResult(url) }
    }
}