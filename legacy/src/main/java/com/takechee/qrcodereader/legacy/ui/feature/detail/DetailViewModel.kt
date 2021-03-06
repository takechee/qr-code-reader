package com.takechee.qrcodereader.legacy.ui.feature.detail

import android.content.*
import android.graphics.Bitmap
import android.util.Patterns
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.takechee.qrcodereader.legacy.R
import com.takechee.qrcodereader.corecomponent.data.prefs.PreferenceStorage
import com.takechee.qrcodereader.corecomponent.result.Event
import com.takechee.qrcodereader.corecomponent.result.fireEvent
import com.takechee.qrcodereader.corecomponent.ui.common.base.BaseViewModel
import com.takechee.qrcodereader.legacy.data.repository.ContentRepository
import com.takechee.qrcodereader.model.Content
import com.takechee.qrcodereader.legacy.ui.Navigator
import com.takechee.qrcodereader.legacy.util.extension.px
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val context: Context,
    private val clipboardManager: ClipboardManager,
    private val args: DetailArgs,
    private val encoder: BarcodeEncoder,
    private val prefs: PreferenceStorage,
    private val repository: ContentRepository,
    private val navigator: DetailNavigator
) : BaseViewModel(), DetailUserEventListener, Navigator by navigator {

    companion object {
        private const val QR_IMAGE_SIZE = 200
    }

    private val contentFlow = repository.getContentFlow(args.contentId)
    private val content = contentFlow.asLiveData(viewModelScope.coroutineContext)

    val uiModel: LiveData<DetailUiModel>

    private val qrImage: LiveData<Bitmap?> = content.map {
        try {
            val size = QR_IMAGE_SIZE.px
            encoder.encodeBitmap(it?.text, BarcodeFormat.QR_CODE, size, size)
        } catch (e: WriterException) {
            null
        }
    }

    private val _event = MutableLiveData<Event<DetailEvent>>()
    val event: LiveData<Event<DetailEvent>>
        get() = _event.distinctUntilChanged()


    // =============================================================================================
    //
    // Initialize
    //
    // =============================================================================================
    init {
        if (prefs.autoLoadNickname) viewModelScope.launch {
            contentFlow.collect { content ->
                if (content == null) return@collect
                if (content.nickname.isNotEmpty) return@collect
                val docTitle = getDocTitle(content.text) ?: return@collect
                repository.updateContent(
                    contentId = args.contentId,
                    nickname = docTitle
                )
            }
        }

        uiModel = MediatorLiveData<DetailUiModel>().apply {
            value = DetailUiModel.EMPTY
            val valueUpdaterObserver = Observer<Any> {
                value = DetailUiModel(
                    qrImage = qrImage.value,
                    content = content.value ?: Content.EMPTY
                )
            }
            listOf(
                qrImage.distinctUntilChanged(),
                content.distinctUntilChanged()
            ).forEach { source ->
                addSource(source, valueUpdaterObserver)
            }
        }.distinctUntilChanged()
    }


    // =============================================================================================
    //
    // Event
    //
    // =============================================================================================
    override fun onShareActionClick() {
        withContent { content ->
            fireEvent {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, content.text)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                DetailEvent.OpenIntent(shareIntent)
            }
        }
    }

    override fun onOpenIntentActionClick() {
        withContent { content ->
            fireEvent {
                val viewIntent = Intent(Intent.ACTION_VIEW, content.text.toUri())
                viewIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val chooserIntent = Intent.createChooser(viewIntent, null)
                DetailEvent.OpenIntent(chooserIntent)
            }
        }
    }

    override fun onOpenUrlActionClick() {
        withContent { content ->
            fireEvent {
                if (prefs.useBrowserApp) {
                    val viewIntent = Intent(Intent.ACTION_VIEW, content.text.toUri())
                    val chooserIntent = Intent.createChooser(viewIntent, null)
                    DetailEvent.OpenUrl.BrowserApp(chooserIntent)
                } else {
                    DetailEvent.OpenUrl.CustomTabs(content.text.toUri())
                }
            }
        }
    }

    override fun onCopyToClipBoardActionClick() {
        withContent { content ->
            // クリップボードに格納するItemを作成
            val item: ClipData.Item = ClipData.Item(content.text)
            // MimeTypeの作成
            val mimeType = arrayOfNulls<String>(1)
            mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST
            //クリップボードに格納するClipDataオブジェクトの作成
            val cd = ClipData(ClipDescription("text_data", mimeType), item)
            clipboardManager.setPrimaryClip(cd)

            Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditNicknameClick() {
        withContent { content ->
            val isWebUrl = Patterns.WEB_URL.matcher(content.text).matches()
            fireEvent {
                DetailEvent.ShowEditNicknameDialog(content.nickname.value, isWebUrl)
            }
        }
    }

    override fun onEditNicknamePositiveClick(nickname: String) {
        viewModelScope.launch {
            repository.updateContent(
                contentId = args.contentId,
                nickname = nickname
            )
        }
    }

    override fun onGetTitleByUrlClick(): LiveData<Event<String>> {
        return liveData(viewModelScope.coroutineContext) {
            val contentText = content.value?.text ?: return@liveData
            getDocTitle(contentText = contentText)?.let { docTitle ->
                emit(Event(docTitle))
            }
        }
    }

    override fun onFavoriteClick(isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateContent(contentId = args.contentId, isFavorite = isFavorite)
        }
    }

    override fun onDeleteClick() {
        viewModelScope.launch {
            repository.delete(contentId = args.contentId)

            Toast.makeText(context, R.string.complete_delete, Toast.LENGTH_SHORT).show()
            navigator.navigatePopBack()
        }
    }


    // =============================================================================================
    //
    // Utility
    //
    // =============================================================================================
    private suspend fun getDocTitle(contentText: String): String? {
        if (!Patterns.WEB_URL.matcher(contentText).matches()) return null

        @Suppress("BlockingMethodInNonBlockingContext")
        return withContext(Dispatchers.IO) {
            try {
                Jsoup.connect(contentText).get()?.title()
            } catch (e: IOException) {
                return@withContext null
            }
        }
    }

    private fun fireEvent(provider: () -> DetailEvent) {
        _event.fireEvent(provider)
    }

    private fun withContent(action: (content: Content) -> Unit) {
        val content = content.value ?: return
        action.invoke(content)
    }
}