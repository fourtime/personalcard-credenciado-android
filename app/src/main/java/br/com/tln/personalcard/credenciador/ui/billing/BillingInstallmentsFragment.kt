package br.com.tln.personalcard.credenciador.ui.billing

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentBillingInstallmentsBinding
import br.com.tln.personalcard.credenciador.extensions.showKeyboad
import br.com.tln.personalcard.credenciador.transformer.TextTransformer

class BillingInstallmentsFragment :
    SessionRequiredBaseFragment<FragmentBillingInstallmentsBinding, BillingInstallmentsViewModel>(
        layoutRes = R.layout.fragment_billing_installments,
        viewModelClass = BillingInstallmentsViewModel::class.java
    ), HasToolbar {

    private var showKeyboard = true

    private val args: BillingInstallmentsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (showKeyboard) {
            binding.billingInstallmentsInstallmentsAmount.showKeyboad()
            showKeyboard = false
        }

        binding.textTransformer = TextTransformer()
        binding.viewModel = viewModel

        viewModel.setTitle(args.title)
        viewModel.setMaxInstallments(args.maxInstallments)
        viewModel.setBillingValue(args.billingValue.toBigDecimal())
    }

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }
}