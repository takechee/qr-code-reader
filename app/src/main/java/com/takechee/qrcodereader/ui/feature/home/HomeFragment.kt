package com.takechee.qrcodereader.ui.feature.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import com.takechee.qrcodereader.databinding.FragmentHomeBinding
import com.takechee.qrcodereader.result.receiveEvent
import com.takechee.qrcodereader.ui.common.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    @Inject
    lateinit var intentIntegrator: IntentIntegrator

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(homeViewModel)

        val historySection = HomeHistorySection(homeViewModel)

        val adapter = GroupAdapter<GroupieViewHolder>()
        binding.contentsView.adapter = adapter
        val list = mutableListOf<Item<*>>()
        list.add(HomeHistoriesItem(historySection))
        adapter.update(list)
        homeViewModel.urls.observe(viewLifecycleOwner) { historySection.update(it) }

        homeViewModel.openReader.receiveEvent(viewLifecycleOwner) {
            intentIntegrator.initiateScan()
        }

        homeViewModel.openURL.receiveEvent(viewLifecycleOwner) { url ->
            findNavController().navigate(HomeFragmentDirections.toResult(url))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)) {
            null -> super.onActivityResult(requestCode, resultCode, data)
            else -> when (val contents = result.contents) {
                null -> Toast.makeText(context, "canceled", Toast.LENGTH_SHORT).show()
                else -> {
                    findNavController().navigate(HomeFragmentDirections.toResult(contents))
                }
            }
        }
    }
}


// =================================================================================================
//
// Module
//
// =================================================================================================
@Module
@Suppress("UNUSED")
abstract class HomeModule {
    @HomePageScoped
    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    internal abstract fun contributeHomeFragment(): HomeFragment
}
