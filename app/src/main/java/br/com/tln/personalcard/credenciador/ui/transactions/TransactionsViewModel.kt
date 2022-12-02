package br.com.tln.personalcard.credenciador.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arrow.core.Either
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.model.TransactionDailySummary
import br.com.tln.personalcard.credenciador.model.TransactionPeriodSummary
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import br.com.tln.personalcard.credenciador.repository.TerminalRepository
import br.com.tln.personalcard.credenciador.webservice.request.StatementListRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class TransactionsViewModel @Inject constructor(
    private val terminalRepository: TerminalRepository,
    sessionRepository: SessionRepository,
    navigator: TransactionsNavigator
) : SessionRequiredBaseViewModel<TransactionsNavigator>(
    sessionRepository = sessionRepository,
    navigator = navigator
) {

    private val _periodSummary = MediatorLiveData<TransactionPeriodSummary>()
    private val _transactionPeriodSummaryLiveData =
        MediatorLiveData<Resource<TransactionPeriodSummary>>()
    private val _pagedListLiveData = MutableLiveData<LiveData<PagedList<TransactionDailySummary>>>()

    val periodSummary: LiveData<TransactionPeriodSummary> = _periodSummary
    val periodSummaryLabel = Transformations.map(periodSummary) {
        it.period
    }
    val periodSummaryTotalValue = Transformations.map(periodSummary) {
        it.totalValue
    }
    val periodSummaryAverageTicketValue = Transformations.map(periodSummary) {
        it.averageTicketValue
    }

    val transactionPeriodSummaryLiveData: MediatorLiveData<Resource<TransactionPeriodSummary>>
        get() =
            _transactionPeriodSummaryLiveData

    val pagedListLiveData: LiveData<LiveData<PagedList<TransactionDailySummary>>> get() = _pagedListLiveData

    fun getTransactionPeriodSummaryLiveData(): LiveData<Resource<TransactionPeriodSummary>> {
        return _transactionPeriodSummaryLiveData
    }

    fun loadPaigedTransactionDailySummary() {

        val from = LocalDateTime.now().minusDays(30)
        val to = LocalDateTime.now()

        val listConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(30)
            .setPageSize(10)
            .setPrefetchDistance(10)
            .build()

        val builder =
            LivePagedListBuilder(
                TransactionDataSource.Factory(
                    viewModel = this,
                    from = from,
                    to = to
                ), listConfig
            )
                .build()

        _pagedListLiveData.postValue(builder)
    }

    fun getTransactionPeriodSummary(
        from: LocalDateTime,
        to: LocalDateTime,
        pageNumber: Int,
        pageSize: Int,
        callback: (transactionPeriodSumary: TransactionPeriodSummary) -> Unit
    ) {

        _transactionPeriodSummaryLiveData.postValue(Resource.loading())

        viewModelScope.launch {

            val account = withContext(Dispatchers.IO) {
                sessionRepository.getAccount()
            } ?: return@launch

            val request = StatementListRequest(
                pageNumber = pageNumber,
                pageSize = pageSize,
                from = from.toString(),
                to = to.toString()
            )

            val result = terminalRepository.getTransactionsLiveData(
                accessToken = account.accessToken,
                request = request
            )

            when (result) {
                is Either.Left -> {
                    onGetTransactionPeriodSummaryDataFailure(throwable = result.a)
                }
                is Either.Right -> {
                    onGetTransactionPeriodSummaryDataSuccess(resource = result.b)

                    val resource: Resource<TransactionPeriodSummary> = result.b
                    resource.data?.let { callback(it) }
                }
            }
        }
    }

    private fun onGetTransactionPeriodSummaryDataFailure(throwable: Throwable) {
        errorNotifier.notify(throwable)
    }

    private fun onGetTransactionPeriodSummaryDataSuccess(resource: Resource<TransactionPeriodSummary>) {
        when (resource.status) {
            Resource.Status.LOADING -> {
                onGetTransactionPeriodSummaryLoading()
            }
            Resource.Status.SUCCESS -> {
                resource.data?.let { onGetTransactionPeriodSummarySuccess(transactionPeriodSumary = it) }
            }
            Resource.Status.ERROR -> {
                onGetTransactionPeriodSummaryError()
            }
        }
    }

    private fun onGetTransactionPeriodSummaryLoading() {
        _transactionPeriodSummaryLiveData.value = Resource.loading()
    }

    private fun onGetTransactionPeriodSummarySuccess(transactionPeriodSumary: TransactionPeriodSummary) {
        _transactionPeriodSummaryLiveData.value = Resource.success(data = transactionPeriodSumary)
        if (transactionPeriodSumary == null) {
            _unknownErrorLiveData.postValue(Event(data = null))
        }
        _periodSummary.postValue(transactionPeriodSumary)
    }

    private fun onGetTransactionPeriodSummaryError() {
        _unknownErrorLiveData.value = Event(data = null)
    }

    fun dailySummaryClicked(dailySummary: TransactionDailySummary) {
        navigator.navigateToDayTransactions(dailySummary)
    }
}