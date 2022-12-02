package br.com.tln.personalcard.credenciador.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import br.com.tln.personalcard.credenciador.di.Injectable
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.extensions.observeEvent

abstract class SessionRequiredBaseFragment<DB : ViewDataBinding, VM : SessionRequiredBaseViewModel<*>>(
    @LayoutRes layoutRes: Int,
    viewModelClass: Class<VM>
) : BaseFragment<DB, VM>(
    layoutRes = layoutRes,
    viewModelClass = viewModelClass
), Injectable {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.sessionMonitorLiveData
            .observe(viewLifecycleOwner) {
            }

        viewModel.authenticationErrorLiveData.observeEvent(viewLifecycleOwner) {
            viewModel.lockSession()
        }

        return view
    }
}