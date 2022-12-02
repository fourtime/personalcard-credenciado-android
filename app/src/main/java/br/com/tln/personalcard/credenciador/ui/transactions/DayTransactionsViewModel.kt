package br.com.tln.personalcard.credenciador.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arrow.core.Either
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.extensions.format
import br.com.tln.personalcard.credenciador.extensions.sha256
import br.com.tln.personalcard.credenciador.model.AnalyticSummary
import br.com.tln.personalcard.credenciador.model.Transaction
import br.com.tln.personalcard.credenciador.provider.ResourceProvider
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import br.com.tln.personalcard.credenciador.repository.TerminalRepository
import br.com.tln.personalcard.credenciador.webservice.request.AnalyticSatatementListRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import java.math.BigDecimal
import javax.inject.Inject

class DayTransactionsViewModel @Inject constructor(
    private val terminalRepository: TerminalRepository,
    private val resourceProvider: ResourceProvider,
    sessionRepository: SessionRepository,
    navigator: DayTransactionsNavigator
) : SessionRequiredBaseViewModel<DayTransactionsNavigator>(
    sessionRepository = sessionRepository,
    navigator = navigator
) {

    private val _periodLabelLiveData = MutableLiveData<String>()
    private val _periodTotalValue = MutableLiveData<BigDecimal>()
    private val _dayLiveData = MutableLiveData<String>()
    private val _passwordErrorMessageLiveData = MutableLiveData<Event<Transaction>>()

    private val _cancelConfirmationLiveData = MutableLiveData<Event<Transaction>>()
    private val _passwordConfirmation = MutableLiveData<String?>()
    private val _analyticSummaryLiveData = MediatorLiveData<Resource<AnalyticSummary>>()
    private val _pagedListLiveData = MutableLiveData<LiveData<PagedList<Transaction>>>()

    val periodLabel: LiveData<String> = _periodLabelLiveData
    val periodTotalValue: LiveData<BigDecimal> = _periodTotalValue
    val day: LiveData<String> = _dayLiveData

    val cancelConfirmationLiveData: LiveData<Event<Transaction>> = _cancelConfirmationLiveData
    val pagedListLiveData: LiveData<LiveData<PagedList<Transaction>>> get() = _pagedListLiveData

    val analyticSummaryLiveData: MediatorLiveData<Resource<AnalyticSummary>>get() = _analyticSummaryLiveData
    val passwordErrorMessageLiveData: LiveData<Event<Transaction>>get() = _passwordErrorMessageLiveData

    fun setData(label: String, totalTicketValue: BigDecimal) {
        _periodLabelLiveData.postValue(label)
        _periodTotalValue.postValue(totalTicketValue)
    }

    fun cancelClicked(transaction: Transaction) {
        _cancelConfirmationLiveData.postValue(Event(transaction))
    }

    fun setConfirmPassword(confirmPassword: String) {
        _passwordConfirmation.value = confirmPassword
    }

    fun loadPagedTransactionDailySummary(period: LocalDateTime) {
        val listConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(30)
            .setPageSize(10)
            .setPrefetchDistance(10)
            .build()

        val builder =
            LivePagedListBuilder(
                DayTransactionsDataSource.Factory(
                    period = period,
                    viewModel = this
                ), listConfig
            )
                .build()

        _pagedListLiveData.postValue(builder)
        _dayLiveData.postValue(period.format("EEEE").capitalize())
    }

    fun getTransactionDay(
        from: LocalDateTime,
        to: LocalDateTime,
        pageNumber: Int,
        pageSize: Int,
        callback: (analyticSummary: AnalyticSummary) -> Unit
    ) {

        _analyticSummaryLiveData.postValue(Resource.loading())

        viewModelScope.launch {

            val account = withContext(Dispatchers.IO) {
                sessionRepository.getAccount()
            } ?: return@launch

            val request = AnalyticSatatementListRequest(
                pageNumber = pageNumber,
                pageSize = pageSize,
                from = from.toString(),
                to = to.toString()
            )

            val result = terminalRepository.getDayTransactionsLiveData(
                accessToken = account.accessToken,
                request = request
            )

            when (result) {
                is Either.Left -> {
                    onGetAnalyticSummaryDataFailure(throwable = result.a)
                }
                is Either.Right -> {
                    onGetAnalyticSummaryDataSuccess(resource = result.b)

                    val resource: Resource<AnalyticSummary> = result.b
                    resource.data?.let { callback(it) }
                }
            }

        }
    }

    private fun onGetAnalyticSummaryDataFailure(throwable: Throwable) {
        errorNotifier.notify(throwable)
    }

    private fun onGetAnalyticSummaryDataSuccess(resource: Resource<AnalyticSummary>) {
        when (resource.status) {
            Resource.Status.LOADING -> {
                onGetAnalyticSummaryLoading()
            }
            Resource.Status.SUCCESS -> {
                resource.data?.let { onGetAnalyticSummarySuccess(analyticSummary = it) }
            }
            Resource.Status.ERROR -> {
                onGetAnalyticSummaryError()
            }
        }
    }

    private fun onGetAnalyticSummaryLoading() {
        _analyticSummaryLiveData.value = Resource.loading()
    }

    private fun onGetAnalyticSummarySuccess(analyticSummary: AnalyticSummary) {
        _analyticSummaryLiveData.value = Resource.success(data = analyticSummary)
        if (analyticSummary == null) {
            _unknownErrorLiveData.postValue(Event(data = null))
        }
        _periodTotalValue.postValue(analyticSummary.totalTicketValue)
    }

    private fun onGetAnalyticSummaryError() {
        _unknownErrorLiveData.value = Event(data = null)
    }

    fun cancelConfirmed(transaction: Transaction): LiveData<Resource<Nothing?>> {
        val liveData = MediatorLiveData<Resource<Nothing?>>()

        val passwordConfirmation = this._passwordConfirmation.value

        if (passwordConfirmation.isNullOrEmpty()) {
            _unknownErrorLiveData.postValue(Event(data = null))
            return liveData
        }

        viewModelScope.launch {

            sessionRepository.getAccountData()?.also { accountData ->

                val message = resourceProvider.getString(R.string.cancel_transaction_invalid_password)

                if (passwordConfirmation.sha256() != accountData.localPassword) {
                    _passwordErrorMessageLiveData.postValue(Event<Transaction>(data = transaction))

                    return@launch
                }

                sessionRepository.getAccount()?.also { account ->
                    val requestLiveData = terminalRepository.getCancelTransactionLiveData(
                        accessToken = account.accessToken,
                        account = account,
                        transaction = transaction
                    )
                    liveData.addSource(requestLiveData) { result ->
                        when (result) {
                            is Either.Left -> {
                                liveData.postValue(Resource.error())
                                errorNotifier.notify(result.a)
                            }
                            is Either.Right -> {
                                val resource: Resource<String> = result.b

                                when (resource.status) {
                                    Resource.Status.LOADING -> {
                                        liveData.postValue(Resource.loading())
                                    }
                                    Resource.Status.SUCCESS -> {
                                        liveData.postValue(Resource.success())
                                    }
                                    Resource.Status.ERROR -> {
                                        liveData.postValue(Resource.error())
                                        _unknownErrorLiveData.postValue(Event(data = null))
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        return liveData
    }

}