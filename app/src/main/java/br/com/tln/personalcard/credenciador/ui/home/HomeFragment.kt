package br.com.tln.personalcard.credenciador.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentHomeBinding
import br.com.tln.personalcard.credenciador.databinding.HomeNavViewHeaderBinding
import br.com.tln.personalcard.credenciador.extensions.hideSoftKeyboard
import br.com.tln.personalcard.credenciador.extensions.observeEvent
import br.com.tln.personalcard.credenciador.ui.home.HomeViewModel.MenuItem.CHANGE_PASSWORD
import br.com.tln.personalcard.credenciador.ui.home.HomeViewModel.MenuItem.EXIT
import br.com.tln.personalcard.credenciador.ui.home.HomeViewModel.MenuItem.SUPPORT
import br.com.tln.personalcard.credenciador.ui.home.HomeViewModel.MenuItem.TRANSACTIONS

class HomeFragment : SessionRequiredBaseFragment<FragmentHomeBinding, HomeViewModel>(
    R.layout.fragment_home,
    HomeViewModel::class.java
), HasToolbar {

    private lateinit var navHeaderBinding: HomeNavViewHeaderBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideSoftKeyboard()

        binding.navView.itemIconTintList = null
        binding.viewModel = viewModel

        navHeaderBinding = HomeNavViewHeaderBinding.bind(binding.navView.getHeaderView(0))
        navHeaderBinding.lifecycleOwner = viewLifecycleOwner
        navHeaderBinding.viewModel = viewModel

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_transactions -> {
                    viewModel.onMenuItemClicked(TRANSACTIONS)
                }
                R.id.nav_support -> {
                    viewModel.onMenuItemClicked(SUPPORT)
                }
                R.id.nav_change_password -> {
                    viewModel.onMenuItemClicked(CHANGE_PASSWORD)
                }
                R.id.nav_exit -> {
                    viewModel.onMenuItemClicked(EXIT)
                }
                else -> return@setNavigationItemSelectedListener false
            }

            binding.drawerLayout.closeDrawer(binding.navView, true)
            true
        }

        viewModel.supportLiveData.observeEvent(viewLifecycleOwner) { event ->
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(event.data))
        }
    }

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.navView, findNavController())
        NavigationUI.setupWithNavController(binding.toolbar, findNavController(), binding.drawerLayout)
    }
}