package br.com.tln.personalcard.credenciador.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.extensions.CryptographyUtils.decrypt
import br.com.tln.personalcard.credenciador.provider.ResourceProvider
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import br.com.tln.personalcard.credenciador.task.RenewTerminalDataTask
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    sessionRepository: SessionRepository,
    private val renewTerminalDataTask: RenewTerminalDataTask,
    private val resourceProvider: ResourceProvider,
    navigator: HomeNavigator
) : SessionRequiredBaseViewModel<HomeNavigator>(
    navigator = navigator,
    sessionRepository = sessionRepository
) {

    private val _supportLiveData = MediatorLiveData<Event<String>>()

    val supportLiveData: LiveData<Event<String>> = _supportLiveData

    val hasActiveSession: LiveData<Boolean> =
        Transformations.map(sessionRepository.getAccountLiveData()) {
            it != null && !it.localPassword.isNullOrEmpty() && !it.locked
        }
    val terminalBusinessName: LiveData<String?> =
        Transformations.map(sessionRepository.getAccountLiveData()) {
            it?.terminal?.businessName
        }

    val terminalCnpj: LiveData<String?> =
        Transformations.map(sessionRepository.getAccountLiveData()) {
            it?.terminal?.cnpj
        }
    val terminalNumber: LiveData<String?> =
        Transformations.map(sessionRepository.getAccountLiveData()) {
            it?.password?.decrypt()
        }
    val acceptPrepaidCard: LiveData<Boolean> =
        Transformations.map(sessionRepository.getAccountLiveData()) {
            it?.terminal?.acceptedPaymentMethods?.any { paymentMethod ->
                paymentMethod == PaymentMethod.PRE_PAID_CARD
            } ?: false
        }
    val acceptPostPaidCard: LiveData<Boolean> =
        Transformations.map(sessionRepository.getAccountLiveData()) {
            it?.terminal?.acceptedPaymentMethods?.any { paymentMethod ->
                paymentMethod == PaymentMethod.POST_PAID_CARD
            } ?: false
        }
    val acceptFleetCard: LiveData<Boolean> =
        Transformations.map(sessionRepository.getAccountLiveData()) {
            it?.terminal?.acceptedPaymentMethods?.any { paymentMethod ->
                paymentMethod == PaymentMethod.FLEET_CARD
            } ?: false
        }

    val mask by lazy {
        val liveData = MediatorLiveData<String>()

        liveData.value = resourceProvider.getString(R.string.mask_dynamic_cpf)

        liveData.addSource(terminalCnpj) {
            if (it != null && it.length > resourceProvider.getInteger(R.integer.cpf_length)) {
                liveData.value = resourceProvider.getString(R.string.mask_dynamic_cnpj)
            } else {
                liveData.value = resourceProvider.getString(R.string.mask_dynamic_cpf)
            }
        }
        liveData
    }

    override fun onCleared() {
        renewTerminalDataTask.stop()
    }

    override fun onSessionNotFound() {
        renewTerminalDataTask.stop()
    }

    override fun onLockedSessionFound() {
        renewTerminalDataTask.stop()
    }

    override fun onSessionFound() {
        renewTerminalDataTask.start()
    }

    fun onMenuItemClicked(menuItem: MenuItem) {
        when (menuItem) {
            MenuItem.TRANSACTIONS -> {
                navigator.navigateToTransactions()
            }
            MenuItem.SUPPORT -> {
                getSupportUrl()
            }
            MenuItem.CHANGE_PASSWORD -> {
                navigator.navigateToChangePassword()
            }
            MenuItem.EXIT -> {
                lockSession()
            }
        }
    }

    fun prePaidCardClicked() {
        navigator.navigateToPrePaidCard(maxInstallments = 1)
    }

    fun postPaidCardClicked() {
        viewModelScope.launch {
            sessionRepository.getTerminal()?.let { terminal ->
                navigator.navigateToPostPaidCard(maxInstallments = terminal.maxInstallment)
            }
        }
    }

    fun fleetCardClicked() {
        navigator.navigateToFleetCard()
    }

    private fun getSupportUrl() {
        viewModelScope.launch {
            sessionRepository.getTerminal()?.let { terminal ->
                _supportLiveData.postValue(Event(terminal.supportUrl))
            }
        }
    }

    enum class MenuItem {
        TRANSACTIONS, SUPPORT, CHANGE_PASSWORD, EXIT
    }
}