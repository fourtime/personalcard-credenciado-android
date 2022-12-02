package br.com.tln.personalcard.credenciador.ui.welcome

import android.os.Bundle
import android.view.View
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.BaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentWelcomeBinding


class WelcomeFragment : BaseFragment<FragmentWelcomeBinding, WelcomeViewModel>(
    R.layout.fragment_welcome,
    WelcomeViewModel::class.java
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
    }
}