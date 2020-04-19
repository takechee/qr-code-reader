package com.takechee.qrcodereader.ui.feature.capture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.takechee.qrcodereader.data.repository.ContentRepository
import com.takechee.qrcodereader.result.Event
import com.takechee.qrcodereader.result.fireEvent
import com.takechee.qrcodereader.ui.Navigator
import com.takechee.qrcodereader.ui.common.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CaptureViewModel @Inject constructor(
    private val navigator: CaptureNavigator,
    private val repository: ContentRepository
) : BaseViewModel(), BarcodeCallback, Navigator by navigator {

    private val _event = MutableLiveData<Event<CaptureEvent>>()
    val event: LiveData<Event<CaptureEvent>>
        get() = _event.distinctUntilChanged()


    // =============================================================================================
    //
    // Event
    //
    // =============================================================================================
    override fun barcodeResult(result: BarcodeResult?) {
        viewModelScope.launch {
            val resultText = result?.text ?: return@launch
            repository.insertCaptureText(resultText) { contentId ->
                navigator.navigateToDetail(contentId)
            }
        }
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        resultPoints?.let {
//            Log.e("barcode", it.toString())
        }
    }


    // =============================================================================================
    //
    // Utility
    //
    // =============================================================================================
    private fun fireEvent(factory: () -> CaptureEvent) {
        _event.fireEvent(factory)
    }

}