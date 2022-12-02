package br.com.tln.personalcard.credenciador.ui.transactions

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.HasToolbar
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseFragment
import br.com.tln.personalcard.credenciador.databinding.FragmentDayTransactionsBinding
import br.com.tln.personalcard.credenciador.extensions.observe
import br.com.tln.personalcard.credenciador.extensions.observeEvent
import br.com.tln.personalcard.credenciador.model.Transaction
import br.com.tln.personalcard.credenciador.transformer.TextTransformer
import br.com.tln.personalcard.credenciador.widget.BottomSheetDialog

class DayTransactionsFragment :
    SessionRequiredBaseFragment<FragmentDayTransactionsBinding, DayTransactionsViewModel>(
        R.layout.fragment_day_transactions,
        DayTransactionsViewModel::class.java
    ), HasToolbar {

    private val args: DayTransactionsFragmentArgs by navArgs()
    private var loadingDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            binding.loading = false
            binding.empty = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textTransformer = TextTransformer()
        binding.viewModel = viewModel

        viewModel.setData(args.label, args.dailySummary.totalValue)

        observeRequestState()

        val adapter = DayTransactionsAdapter { transaction ->
            viewModel.cancelClicked(transaction)
        }

        binding.recyclerView.adapter = adapter

        viewModel.pagedListLiveData.observe(viewLifecycleOwner) { pagedList ->
            pagedList.observe(this) { transaction ->
                adapter.submitList(transaction)
            }
        }

        viewModel.loadPagedTransactionDailySummary(args.dailySummary.date)

        viewModel.cancelConfirmationLiveData.observeEvent(viewLifecycleOwner) { event ->
            val transaction = event.data

            if (transaction.cardId.isNullOrEmpty()) {
                transactionNotCancellable()
                return@observeEvent
            }

            transactionCancellable(transaction, adapter)
        }

        viewModel.passwordErrorMessageLiveData.observeEvent(viewLifecycleOwner) { event ->
            val transaction = event.data

            if (transaction.cardId.isNullOrEmpty()) {
                transactionNotCancellable()
                return@observeEvent
            }

            invalidPassword(transaction, adapter)
        }
    }

    private fun observeRequestState() {
        viewModel.analyticSummaryLiveData.observe(viewLifecycleOwner) { resource ->
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

    private fun transactionCancellable(transaction: Transaction, adapter: DayTransactionsAdapter) {
        val textTransformer = TextTransformer()
        BottomSheetDialog(requireContext())
            .show {
                showCloseButton(true)
                title(getString(R.string.day_transactions_cancel_confirmation_title))
                message(
                    getString(
                        R.string.day_transactions_cancel_confirmation_message,
                        textTransformer.currencyFormat(transaction.value),
                        transaction.cardLastDigits,
                        transaction.nsuHost,
                        transaction.nsuAuthorization
                    )
                )
                confirmText(getString(R.string.day_transactions_cancel_confirmation_close))
                dangerText(getString(R.string.day_transactions_cancel_confirmation_confirm)) {
                    it.dismiss()
                    confirmCancel(transaction, adapter)
                }
            }
    }

    private fun confirmCancel(transaction: Transaction, adapter: DayTransactionsAdapter){
        BottomSheetDialog(requireContext())
            .show {
                showCloseButton(true)
                title(getString(R.string.day_transactions_cancel_confirmation_password_title))
                input(true)
                confirmText(getString(R.string.day_transactions_cancel_confirmation_password_confirm)) {
                    viewModel.setConfirmPassword(input().toString())
                    it.dismiss()
                    viewModel.cancelConfirmed(transaction)
                        .observe(viewLifecycleOwner) { resource ->
                            when (resource.status) {
                                Resource.Status.LOADING -> {
                                    transaction.canceling = true
                                    adapter.changeItem(transaction)
                                }
                                Resource.Status.SUCCESS -> {
                                    transaction.canceling = false
                                    viewModel.loadPagedTransactionDailySummary(args.dailySummary.date)
                                    transactionCanceledSuccessfully()
                                }
                                Resource.Status.ERROR -> {
                                    transaction.canceling = false
                                    adapter.changeItem(transaction)
                                }
                            }
                        }
                }
            }
    }

    private fun transactionNotCancellable() {
        BottomSheetDialog(requireContext())
            .show {
                showCloseButton(true)
                title(getString(R.string.day_transactions_not_cancellable_title))
                message(getString(R.string.day_transactions_not_cancellable_message))
                confirmText(getString(R.string.day_transactions_not_cancellable_close))
            }
    }

    private fun invalidPassword(transaction: Transaction, adapter: DayTransactionsAdapter) {
        BottomSheetDialog(requireContext())
            .show {
                setCancelable(false)
                message(getString(R.string.cancel_transaction_invalid_password))
                confirmText(getString(R.string.day_transactions_not_cancellable_close)) {
                    transactionCancellable(transaction, adapter)
                    dismiss()
                }
            }
    }

    private fun transactionCanceledSuccessfully() {
        BottomSheetDialog(requireContext())
            .show {
                message(getString(R.string.day_transactions_cancellable_message))
                confirmText(getString(R.string.day_transactions_cancellable_close))
            }
    }

    override fun setupToolbar() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
    }
}