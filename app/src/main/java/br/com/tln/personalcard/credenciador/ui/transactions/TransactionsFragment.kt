package br.com.tln.personalcard.credenciador.ui.transactions

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentTransactionsBinding
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.transformer.TextTransformer

class TransactionsFragment : SessionRequiredBaseFragment<FragmentTransactionsBinding, TransactionsViewModel>(
    R.layout.fragment_transactions,
    TransactionsViewModel::class.java
), HasToolbar {

    private var loadingDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            binding.loading = false
            binding.empty = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textTransformer = TextTransformer()
        binding.viewModel = viewModel

        viewModel.loadPaigedTransactionDailySummary()

        observeRequestState()

        val adapter = TransactionsAdapter { summary ->
            viewModel.dailySummaryClicked(summary)
        }

        binding.recyclerView.adapter = adapter


        viewModel.pagedListLiveData.observe(viewLifecycleOwner) { pagedList ->
            pagedList.observe(this) { transaction ->
                adapter.submitList(transaction)
            }
        }
    }

    private fun observeRequestState() {
        viewModel.transactionPeriodSummaryLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                    showLoading()
                }
                Resource.Status.SUCCESS -> {
                    hideLoading()
                }
                Resource.Status.ERROR -> {
                    hideLoading()
                }
            }
        }
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

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }
}