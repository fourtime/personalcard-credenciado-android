package br.com.tln.personalcard.credenciador.ui.changepassword

import android.os.Bundle
import android.view.View
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentChangePasswordBinding
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.widget.BottomSheetDialog
import br.com.tln.personalcard.credenciador.widget.ErrorToast

class ChangePasswordFragment :
    SessionRequiredBaseFragment<FragmentChangePasswordBinding, ChangePasswordViewModel>(
        R.layout.fragment_change_password,
        ChangePasswordViewModel::class.java
    ) {

    private var errorToast: ErrorToast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel

        viewModel.changePasswordLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                }
                Resource.Status.SUCCESS -> {
                    successDialog()
                }
                Resource.Status.ERROR -> {
                }
            }
        }
    }

    override fun showErrorMessage(message: String) {
        errorToast?.dismiss()

        if (message.isBlank()) {
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

    fun successDialog() {
        BottomSheetDialog(requireContext())
            .show {
                icon(R.drawable.change_password_success)
                message(getString(R.string.change_password_dialog_message_confirm_success))
                confirmText(getString(R.string.change_password_dialog_button_confirm_success)) {
                    dismiss()
                    viewModel.navigator.navigateToHome()
                }
                setCancelable(false)
            }
    }
}