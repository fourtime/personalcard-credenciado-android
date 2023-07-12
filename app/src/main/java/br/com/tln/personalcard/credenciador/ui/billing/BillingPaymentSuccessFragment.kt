package br.com.tln.personalcard.credenciador.ui.billing

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentBillingPaymentSuccessBinding
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.extensions.hideSoftKeyboard
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.transformer.TextTransformer
import br.com.tln.personalcard.credenciador.widget.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_billing_value.*

class BillingPaymentSuccessFragment : SessionRequiredBaseFragment<FragmentBillingPaymentSuccessBinding, BillingPaymentSuccessViewModel>(
        layoutRes = R.layout.fragment_billing_payment_success,
        viewModelClass = BillingPaymentSuccessViewModel::class.java
), HasToolbar {

    private val args: BillingPaymentSuccessFragmentArgs by navArgs()
    private var loadingDialog: ProgressDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideSoftKeyboard()

        binding.viewModel = viewModel

        viewModel.setPaymentData(args.cardPayment, args.billingValue)
    }

    override fun setupToolbar() {
        //val appBarConfiguration = AppBarConfiguration(setOf(R.id.billingPaymentSuccessFragment))
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }
}