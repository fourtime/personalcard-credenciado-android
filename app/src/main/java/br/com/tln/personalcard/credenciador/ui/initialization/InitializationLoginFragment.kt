package br.com.tln.personalcard.credenciador.ui.initialization

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.BaseFragment
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.databinding.FragmentInitializationLoginBinding
import br.com.tln.personalcard.credenciador.extensions.hideSoftKeyboard
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.extensions.observeEvent
import br.com.tln.personalcard.credenciador.webservice.response.OperatorLinks
import br.com.tln.personalcard.credenciador.widget.BottomSheetDialog
import br.com.tln.personalcard.credenciador.widget.ErrorToast

class InitializationLoginFragment :
    BaseFragment<FragmentInitializationLoginBinding, InitializationLoginViewModel>(
        R.layout.fragment_initialization_login, InitializationLoginViewModel::class.java
    ) {

    private var loadingDialog: ProgressDialog? = null

    private var errorToast: ErrorToast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel

        viewModel.needHelpLiveData.observeEvent(viewLifecycleOwner) {
            onNeedHelpClick()
        }

        viewModel.loginLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                    showLoading()
                }
                Resource.Status.SUCCESS -> {
                    hideLoading()
                    hideSoftKeyboard()
                }
                Resource.Status.ERROR -> {
                    hideLoading()
                    hideSoftKeyboard()
                }
            }
        }

        viewModel.operatorLinksLiveData.observe(this) {
            // Não fazer nada aqui. Só está sendo utilizado para inicializar.
        }

        viewModel.username.observe(viewLifecycleOwner) {
           showErrorMessage(message = "")
        }

        viewModel.password.observe(viewLifecycleOwner) {
            showErrorMessage(message = "")
        }
    }

    override fun showErrorMessage(message: String) {
        errorToast?.dismiss()

        if(message.isBlank()) {
            return
        }

        this.errorToast = ErrorToast.show(
            context = requireContext(),
            view = binding.root,
            error = message,
            indeterminate = true
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        errorToast?.dismiss()
    }

    private fun onNeedHelpClick() {
        BottomSheetDialog(requireContext())
            .show {
                showCloseButton(true)
                title(getString(R.string.initialization_login_need_help_title))
                message(getString(R.string.initialization_login_need_help_message))
                confirmText(getString(R.string.initialization_login_need_help_confirm)) {
                    val liveData = viewModel.operatorLinksLiveData
                    val observer = object : Observer<Resource<OperatorLinks>> {
                        override fun onChanged(resource: Resource<OperatorLinks>?) {
                            if (resource == null) return

                            when (resource.status) {
                                Resource.Status.LOADING -> {
                                    showLoading()
                                }
                                Resource.Status.SUCCESS -> {
                                    liveData.removeObserver(this)
                                    hideLoading()

                                    resource.data?.run {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(this.contactUrl)
                                        startActivity(intent)
                                    }
                                }
                                Resource.Status.ERROR -> {
                                    liveData.removeObserver(this)
                                    hideLoading()
                                }
                            }
                        }
                    }

                    liveData.observe(viewLifecycleOwner, observer)
                }
            }
    }

    private fun showLoading() {
        val loadingDialog = this.loadingDialog ?: ProgressDialog(context).also {
            it.setCancelable(false)
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
}