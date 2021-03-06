package com.takechee.qrcodereader.legacy.ui.feature.capture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.takechee.qrcodereader.legacy.data.repository.ContentRepository
import com.takechee.qrcodereader.corecomponent.result.Event
import com.takechee.qrcodereader.corecomponent.ui.common.base.BaseViewModel
import com.takechee.qrcodereader.legacy.ui.Navigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class CaptureViewModel @Inject constructor(
    private val navigator: CaptureNavigator,
    private val repository: ContentRepository
) : BaseViewModel(), BarcodeCallback, Navigator by navigator {

    private val _event = MutableLiveData<Event<CaptureEvent>>()
    val event: LiveData<Event<CaptureEvent>>
        get() = _event.distinctUntilChanged()

    private var captureResultJob: Job? = null


    // =============================================================================================
    //
    // Event
    //
    // =============================================================================================
    override fun barcodeResult(result: BarcodeResult?) {
        if (captureResultJob?.isActive == true) return

        captureResultJob = viewModelScope.launch {
            val resultText = result?.text ?: return@launch
            repository.upsertCaptureText(resultText) { contentId ->
                navigator.navigateToDetail(contentId)
            }
        }
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        resultPoints?.let {
//            Log.e("barcode", it.toString())
        }
    }
}