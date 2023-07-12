package br.com.tln.personalcard.credenciador.ui.billing

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentBillingQrCodeReaderBinding
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.extensions.hideSoftKeyboard
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.widget.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class BillingQrCodeReaderFragment : SessionRequiredBaseFragment<FragmentBillingQrCodeReaderBinding, BillingQrCodeReaderViewModel>(
    layoutRes = R.layout.fragment_billing_qr_code_reader,
    viewModelClass = BillingQrCodeReaderViewModel::class.java
), HasToolbar, ZXingScannerView.ResultHandler {

    private val args: BillingQrCodeReaderFragmentArgs by navArgs()
    private var loadingDialog: ProgressDialog? = null
    private var cameraStarted = false

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            binding.reader.startCamera()
        } else {
        }
    }

    private fun askPermission() {
        when {
            ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.CAMERA) -> {
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideSoftKeyboard()

        binding.viewModel = viewModel

        val cardType = PaymentMethod.fromId(args.cardType) ?: PaymentMethod.PRE_PAID_CARD
        when(cardType) {
            PaymentMethod.PRE_PAID_CARD -> binding.toolbar.setTitle(getString(R.string.home_pre_paid_card))
            PaymentMethod.POST_PAID_CARD -> binding.toolbar.setTitle(getString(R.string.home_post_paid_card))
            PaymentMethod.FLEET_CARD -> binding.toolbar.setTitle(getString(R.string.home_fleet_card))
        }
        viewModel.setCardType(cardType)
        viewModel.setBillingValue(args.billingValue.toBigDecimal())
        viewModel.setInstallments(args.installments)
        viewModel.setPaymentToken(args.paymentToken)

        viewModel.cardPaymentLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                    showLoading()
                }
                Resource.Status.SUCCESS -> {
                    hideLoading()
                    resource.data?.let {
                        viewModel.navigateToCardPayment(it.bearerName)
                    }.also {
                        binding.error = true
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoading()
                }
            }
        }

        binding.reader.setFormats(listOf(BarcodeFormat.QR_CODE))
        binding.reader.setResultHandler(this)
        askPermission()
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

    override fun handleResult(rawResult: Result) {
        val qrToken = rawResult.text
        viewModel.navigateToCardPayment(qrToken)
    }

    override fun onResume() {
        super.onResume()

        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}