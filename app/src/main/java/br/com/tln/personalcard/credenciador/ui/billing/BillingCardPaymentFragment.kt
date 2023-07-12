package br.com.tln.personalcard.credenciador.ui.billing

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentBillingCardPaymentBinding
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.extensions.hideSoftKeyboard
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.transformer.TextTransformer
import br.com.tln.personalcard.credenciador.widget.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_billing_card_payment.*
import kotlinx.android.synthetic.main.fragment_billing_value.*
import kotlinx.android.synthetic.main.fragment_billing_value.toolbar


class BillingCardPaymentFragment : SessionRequiredBaseFragment<FragmentBillingCardPaymentBinding, BillingCardPaymentViewModel>(
        layoutRes = R.layout.fragment_billing_card_payment,
        viewModelClass = BillingCardPaymentViewModel::class.java
), HasToolbar {

    private val args: BillingCardPaymentFragmentArgs by navArgs()
    private var loadingDialog: ProgressDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideSoftKeyboard()

        binding.textTransformer = TextTransformer()
        binding.viewModel = viewModel

        val cardType = PaymentMethod.fromId(args.cardType) ?: PaymentMethod.PRE_PAID_CARD
        when(cardType) {
            PaymentMethod.PRE_PAID_CARD -> this.toolbar.setTitle(getString(R.string.home_pre_paid_card))
            PaymentMethod.POST_PAID_CARD -> this.toolbar.setTitle(getString(R.string.home_post_paid_card))
            PaymentMethod.FLEET_CARD -> this.toolbar.setTitle(getString(R.string.home_fleet_card))
        }
        viewModel.setCardType(cardType)
        viewModel.setPaymentToken(args.paymentToken)
        viewModel.setBillingValue(args.billingValue.toBigDecimal())
        viewModel.setInstallments(args.installments)
        viewModel.setQrCodeToken(args.qrcodeToken)

        viewModel.carPaymentLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                    showLoading()
                }
                Resource.Status.SUCCESS -> {
                    hideLoading()
                    resource.data?.let {
                        viewModel.navigator.navigateToPaymentSuccess(cardPayment = it, billingValue = args.billingValue)
                    }.also {
                        binding.error = true
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoading()
                }
            }
        }
    }

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }

    private fun showLoading() {
        val loadingDialog = this.loadingDialog ?: ProgressDialog(context).also {
            it.setCancelable(true)
            it.setTitle(R.string.app_name)
            it.setMessage(getString(R.string.dialog_loading))
            it.show()
        }

        this.loadingDialog = loadingDialog

        loadingDialog.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss().also { loadingDialog = null }
    }

    private fun showRetry() {
        binding.error = true
    }
}