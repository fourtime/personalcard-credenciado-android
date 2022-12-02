package br.com.tln.personalcard.credenciador.ui.billing

import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentBillingQrCodeBinding
import br.com.tln.personalcard.credenciador.extensions.hideSoftKeyboard
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.transformer.TextTransformer
import com.bumptech.glide.Glide

class BillingQrCodeFragment : SessionRequiredBaseFragment<FragmentBillingQrCodeBinding, BillingQrCodeViewModel>(
    layoutRes = R.layout.fragment_billing_qr_code,
    viewModelClass = BillingQrCodeViewModel::class.java
), HasToolbar {

    private val args: BillingQrCodeFragmentArgs by navArgs()

    private val retryClickListener = View.OnClickListener {
        getQrCode()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideSoftKeyboard()

        binding.retryClickListener = retryClickListener
        binding.textTransformer = TextTransformer()
        binding.viewModel = viewModel

        viewModel.setBillingValue(args.billingValue.toBigDecimal())
        viewModel.setInstallments(args.installments)

        getQrCode()
    }

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }

    private fun getQrCode() {
        viewModel.getQrCodeLiveData(installments = args.installments, billingValue = args.billingValue.toDouble())
            .observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    Resource.Status.LOADING -> {
                        showLoading()
                    }
                    Resource.Status.SUCCESS -> {
                        hideLoading()
                        val data = resource.data ?: return@observe

                        showData(data)
                    }
                    Resource.Status.ERROR -> {
                        hideLoading()
                        showRetry()
                    }
                }
            }
    }

    private fun showLoading() {
        binding.error = false
        binding.loading = true
    }

    private fun hideLoading() {
        binding.loading = false
    }

    private fun showRetry() {
        binding.error = true
    }

    private fun showData(data: String) {
        binding.error = false

        val imageByteArray = Base64.decode(data, Base64.DEFAULT)

        Glide.with(this)
            .load(imageByteArray)
            .into(binding.billingQrCodeQrCode)
    }
}