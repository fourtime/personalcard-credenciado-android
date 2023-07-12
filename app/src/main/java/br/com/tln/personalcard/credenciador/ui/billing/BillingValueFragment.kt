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
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import kotlinx.android.synthetic.main.fragment_billing_value.*

class BillingValueFragment : SessionRequiredBaseFragment<FragmentBillingValueBinding, BillingValueViewModel>(
    layoutRes = R.layout.fragment_billing_value,
    viewModelClass = BillingValueViewModel::class.java
), HasToolbar {

    private val args: BillingValueFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel

        val cardType = PaymentMethod.fromId(args.cardType) ?: PaymentMethod.PRE_PAID_CARD
        when(cardType) {
            PaymentMethod.PRE_PAID_CARD -> this.toolbar.setTitle(getString(R.string.home_pre_paid_card))
            PaymentMethod.POST_PAID_CARD -> this.toolbar.setTitle(getString(R.string.home_post_paid_card))
            PaymentMethod.FLEET_CARD -> this.toolbar.setTitle(getString(R.string.home_fleet_card))
        }
        viewModel.setCardType(cardType)
        viewModel.setMaxInstallments(args.maxInstallments)
    }

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }
}