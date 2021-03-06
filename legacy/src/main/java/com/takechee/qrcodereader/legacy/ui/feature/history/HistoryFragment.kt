package com.takechee.qrcodereader.legacy.ui.feature.history

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.takechee.qrcodereader.legacy.R
import com.takechee.qrcodereader.legacy.databinding.FragmentHistoryBinding
import com.takechee.qrcodereader.legacy.ui.MainNavigationFragment
import com.takechee.qrcodereader.legacy.util.extension.simpleItemAnimatorEnabled
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

class HistoryFragment : MainNavigationFragment(R.layout.fragment_history) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: HistoryViewModel by viewModels { viewModelFactory }


    // =============================================================================================
    //
    // Lifecycle
    //
    // =============================================================================================
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHistoryBinding.bind(view).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setupNavigation(viewModel)

        binding.filterToggleFab.doOnLayout {
            binding.historyListView.updatePadding(
                bottom = binding.historyListView.paddingBottom
                        + it.marginTop + it.height + it.marginBottom
            )
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_search -> viewModel.onSearchClick()
                else -> return@setOnMenuItemClickListener false
            }
            true
        }

        val section = HistorySection(viewModel)
        binding.historyListView.apply {
            adapter = GroupAdapter<GroupieViewHolder>().apply { add(section) }
            simpleItemAnimatorEnabled(false)
        }

        viewModel.contents.observe(viewLifecycleOwner) { contents -> section.update(contents) }
    }
}


// =================================================================================================
//
// Module
//
// =================================================================================================
@Module
@Suppress("UNUSED")
abstract class HistoryModule {
    @HistoryPageScoped
    @ContributesAndroidInjector(modules = [HistoryFragmentModule::class])
    internal abstract fun contributeHistoryFragment(): HistoryFragment
}
