package br.com.tln.personalcard.credenciador.ui.initialization

import android.os.Bundle
import android.view.View
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.BaseFragment
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.databinding.FragmentInitializationCreatePasswordBinding
import br.com.tln.personalcard.credenciador.extensions.observe

class InitializationCreatePasswordFragment :
    BaseFragment<FragmentInitializationCreatePasswordBinding, InitializationCreatePasswordViewModel>(
        R.layout.fragment_initialization_create_password, InitializationCreatePasswordViewModel::class.java
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel

        viewModel.createPasswordLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                }
                Resource.Status.SUCCESS -> {
                }
                Resource.Status.ERROR -> {
                }
            }
        }
    }
}