package br.com.tln.personalcard.credenciador.ui.unlock

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.browser.customtabs.CustomTabsIntent
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentUnlockSessionBinding
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.extensions.observeEvent
import br.com.tln.personalcard.credenciador.widget.BottomSheetDialog

class UnlockSessionFragment :
    SessionRequiredBaseFragment<FragmentUnlockSessionBinding, UnlockSessionViewModel>(
        R.layout.fragment_unlock_session,
        UnlockSessionViewModel::class.java
    ) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel

        viewModel.unlockSessionLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                }
                Resource.Status.SUCCESS -> {
                }
                Resource.Status.ERROR -> {
                    val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
                    binding.unlockSessionPassword.startAnimation(shake)
                }
            }
        }

        viewModel.forgotPasswordLiveData.observeEvent(viewLifecycleOwner) {
            BottomSheetDialog(requireContext())
                .show {
                    showCloseButton(true)
                    title(getString(R.string.unlock_session_forgot_password_title))
                    message(getString(R.string.unlock_session_forgot_password_message))
                    confirmText(getString(R.string.unlock_session_forgot_password_confirm)) {

                        viewModel.getSupportUrl()

                        viewModel.supportLiveData.observeEvent(viewLifecycleOwner) { event ->
                            val builder = CustomTabsIntent.Builder()
                            val customTabsIntent = builder.build()
                            customTabsIntent.launchUrl(context, Uri.parse(event.data))
                        }
                    }
                }
        }
    }

}