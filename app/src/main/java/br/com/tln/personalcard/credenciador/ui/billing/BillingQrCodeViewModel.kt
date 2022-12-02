package br.com.tln.personalcard.credenciador.ui.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import arrow.core.Either
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import br.com.tln.personalcard.credenciador.repository.TerminalRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

class BillingQrCodeViewModel @Inject constructor(
    private val terminalRepository: TerminalRepository,
    sessionRepository: SessionRepository,
    navigator: BillingQrCodeNavigator
) : SessionRequiredBaseViewModel<BillingQrCodeNavigator>(
    sessionRepository = sessionRepository,
    navigator = navigator
) {

    val billingValue = MutableLiveData<BigDecimal>()
    val installments = MutableLiveData<Int>()

    fun setBillingValue(value: BigDecimal) {
        billingValue.postValue(value)
    }

    fun setInstallments(installments: Int) {
        this.installments.postValue(installments)
    }

    fun finishClicked() {
        navigator.navigateToHome()
    }

    fun getQrCodeLiveData(installments: Int, billingValue: Double): LiveData<Resource<String>> {
        val liveData = MediatorLiveData<Resource<String>>()

        viewModelScope.launch {
            sessionRepository.getAccount()?.also { account ->
                val requestLiveData = terminalRepository.getBillingQrCodeLiveData(
                    accessToken = account.accessToken,
                    installments = installments,
                    billingValue = billingValue
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
                                    resource.data?.run {
                                        liveData.postValue(Resource.success(this))
                                    } ?: run {
                                        liveData.postValue(Resource.error())
                                        _unknownErrorLiveData.postValue(Event(data = null))
                                    }
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

        return liveData
    }
}