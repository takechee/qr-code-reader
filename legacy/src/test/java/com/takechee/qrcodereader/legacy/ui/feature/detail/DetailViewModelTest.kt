package com.takechee.qrcodereader.legacy.ui.feature.detail

import android.content.ClipboardManager
import android.content.Context
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.takechee.qrcodereader.corecomponent.data.prefs.PreferenceStorage
import com.takechee.qrcodereader.legacy.data.repository.ContentRepository
import com.takechee.qrcodereader.widget.component.MockkRule
import com.takechee.qrcodereader.widget.component.ViewModelTestRule
import io.mockk.impl.annotations.MockK
import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule

class DetailViewModelTest {

    // =============================================================================================
    //
    // Rule
    //
    // =============================================================================================
    @get:Rule
    val viewModelTestRule: TestRule = ViewModelTestRule()

    @get:Rule
    val mockkRule = MockkRule(this)


    // =============================================================================================
    //
    // Mock
    //
    // =============================================================================================
    @MockK(relaxed = true)
    lateinit var context: Context

    @MockK(relaxed = true)
    lateinit var clipboardManager: ClipboardManager

    @MockK(relaxed = true)
    lateinit var args: DetailArgs

    @MockK(relaxed = true)
    lateinit var encoder: BarcodeEncoder

    @MockK(relaxed = true)
    lateinit var prefs: PreferenceStorage

    @MockK(relaxed = true)
    lateinit var repository: ContentRepository

    @MockK(relaxed = true)
    lateinit var navigator: DetailNavigator


    // =============================================================================================
    //
    // Test
    //
    // =============================================================================================
//    @Test
//    fun openUrlActionClick_UseBrowserApp_True() {
//        val dummyContent = createDummyContent()
//        coEvery { args.contentId } returns DUMMY_CONTENT_ID1
//        coEvery { repository.getContentFlow(DUMMY_CONTENT_ID1) } returns flowOf(dummyContent)
//        coEvery { prefs.useBrowserApp } returns true
//        val viewModel = DetailViewModel(
//            context = context,
//            clipboardManager = clipboardManager,
//            args = args,
//            encoder = encoder,
//            prefs = prefs,
//            repository = repository,
//            navigator = navigator
//        )
//
//        val testObserver = viewModel
//            .event
//            .test()
//
//        viewModel.onOpenUrlActionClick()
//
//        val valueHistory = testObserver.valueHistory()
//        valueHistory[0].also { result ->
//            val viewIntent = Intent(Intent.ACTION_VIEW, dummyContent.text.toUri())
//            val chooserIntent = Intent.createChooser(viewIntent, null)
//            result shouldBe DetailEvent.OpenUrl.BrowserApp(chooserIntent)
//        }
//    }

}