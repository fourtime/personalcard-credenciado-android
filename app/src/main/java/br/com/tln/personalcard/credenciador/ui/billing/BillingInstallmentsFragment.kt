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
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.extensions.showKeyboad
import br.com.tln.personalcard.credenciador.transformer.TextTransformer
import kotlinx.android.synthetic.main.fragment_billing_installments.*

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

        val cardType = PaymentMethod.fromId(args.cardType) ?: PaymentMethod.PRE_PAID_CARD
        when(cardType) {
            PaymentMethod.PRE_PAID_CARD -> this.toolbar.setTitle(getString(R.string.home_pre_paid_card))
            PaymentMethod.POST_PAID_CARD -> this.toolbar.setTitle(getString(R.string.home_post_paid_card))
            PaymentMethod.FLEET_CARD -> this.toolbar.setTitle(getString(R.string.home_fleet_card))
        }
        viewModel.setCardType(cardType)
        viewModel.setMaxInstallments(args.maxInstallments)
        viewModel.setBillingValue(args.billingValue.toBigDecimal())
    }

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }
}