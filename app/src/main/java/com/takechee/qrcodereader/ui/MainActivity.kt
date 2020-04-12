package com.takechee.qrcodereader.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.takechee.qrcodereader.R
import com.takechee.qrcodereader.databinding.ActivityMainBinding
import com.takechee.qrcodereader.ui.common.base.BaseActivity
import com.takechee.qrcodereader.ui.feature.home.HomeFragmentDirections
import dagger.Module

class MainActivity : BaseActivity(), NavigationHost {

    companion object {
        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.navigation_home,
            R.id.navigation_history,
            R.id.navigation_settings
        )
    }

    private var _navHost: NavHostFragment? = null
    private val navHost: NavHostFragment
        get() = _navHost ?: findNavHostFragment().also {
            _navHost = it
        }

    private var _navController: NavController? = null
    private val navController: NavController
        get() = _navController ?: navHost.navController.also {
            _navController = it
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
        binding.bottomNavigation.setupWithNavController(navController)
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun findNavHostFragment(): NavHostFragment {
        return supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }
}

@Module
@Suppress("UNUSED")
abstract class MainActivityModule {

}
