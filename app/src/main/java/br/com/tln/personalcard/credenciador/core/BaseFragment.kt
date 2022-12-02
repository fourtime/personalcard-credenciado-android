package br.com.tln.personalcard.credenciador.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.di.Injectable
import br.com.tln.personalcard.credenciador.extensions.observeEvent
import br.com.tln.personalcard.credenciador.widget.BottomSheetDialog
import javax.inject.Inject

abstract class BaseFragment<DB : ViewDataBinding, VM : BaseViewModel<*>>(
    @LayoutRes private val layoutRes: Int,
    private val viewModelClass: Class<VM>
) : Fragment(),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModelProvider by lazy {
        ViewModelProviders.of(this, viewModelFactory)
    }
    protected val viewModel by lazy {
        viewModelProvider.get(viewModelClass)
    }

    protected lateinit var binding: DB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewModel.setNavController(findNavController())
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        if (this is HasToolbar) {
            setupToolbar()
        }

        viewModel.connectionErrorLiveData.observeEvent(viewLifecycleOwner) {
            showConnectionError()
        }

        viewModel.unknownErrorLiveData.observeEvent(viewLifecycleOwner) {
            showUnkownError()
        }

        viewModel.errorMessageLiveData.observeEvent(viewLifecycleOwner) {
            showErrorMessage(it.data)
        }

        return binding.root
    }

    protected open fun showConnectionError() {
        BottomSheetDialog(requireContext())
            .show {
                icon(R.drawable.no_connection)
                message(getString(R.string.dialog_no_connection_error_message))
                confirmText(getString(R.string.dialog_no_connection_button_text))
            }
    }

    protected open fun showUnkownError() {
        BottomSheetDialog(requireContext())
            .show {
                icon(R.drawable.error)
                message(getString(R.string.dialog_generic_error_message))
                confirmText(getString(R.string.dialog_generic_error_button_text))
            }
    }

    protected open fun showErrorMessage(message: String) {
        BottomSheetDialog(requireContext())
            .show {
                icon(R.drawable.error)
                message(message)
                confirmText(getString(R.string.dialog_custom_error_button_text))
            }
    }
}