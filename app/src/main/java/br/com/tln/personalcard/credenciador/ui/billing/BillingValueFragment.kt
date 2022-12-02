package br.com.tln.personalcard.credenciador.ui.billing

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentBillingValueBinding

class BillingValueFragment : SessionRequiredBaseFragment<FragmentBillingValueBinding, BillingValueViewModel>(
    layoutRes = R.layout.fragment_billing_value,
    viewModelClass = BillingValueViewModel::class.java
), HasToolbar {

    private val args: BillingValueFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel

        viewModel.setTitle(args.title)
        viewModel.setMaxInstallments(args.maxInstallments)
    }

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }
}