package com.takechee.qrcodereader.ui.feature.onboading

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.takechee.qrcodereader.R
import com.takechee.qrcodereader.databinding.ActivityOnboadingBinding
import com.takechee.qrcodereader.di.ViewModelKey
import com.takechee.qrcodereader.result.receiveEvent
import com.takechee.qrcodereader.ui.common.base.BaseActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class OnboadingActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: OnboadingViewModel by viewModels { viewModelFactory }


    // =============================================================================================
    //
    // Lifecycle
    //
    // =============================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityOnboadingBinding>(
            this,
            R.layout.activity_onboading
        )
        binding.viewModel = viewModel

        binding.pager.adapter = GroupAdapter<GroupieViewHolder>().apply {
            update(OnboadingPage.items())
        }

        binding.actionArea.doOnLayout {
            binding.pager.setPageTransformer { page, _ ->
                page.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = it.height
                }
            }
        }

        viewModel.event.receiveEvent(this) { event ->
            when (event) {
                is OnboadingEvent.Destination -> {
                    startActivity(event.intent)
                    finish()
                }
            }
        }
    }
}


@Module
@Suppress("UNUSED")
abstract class OnboadingActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(OnboadingViewModel::class)
    abstract fun bindOnboadingViewModel(viewModel: OnboadingViewModel): ViewModel
}