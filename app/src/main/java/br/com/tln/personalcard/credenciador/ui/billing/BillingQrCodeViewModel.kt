package br.com.tln.personalcard.credenciador.ui.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import arrow.core.Either
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.model.QrCodePayment
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

    val cardType = MutableLiveData<PaymentMethod>()
    val billingValue = MutableLiveData<BigDecimal>()
    val installments = MutableLiveData<Int>()

    val paymentToken = MutableLiveData<String?>()
    val showReadCardOption: LiveData<Boolean> = Transformations.map(paymentToken) {
        !it.isNullOrEmpty()
    }
    val showCardPaymentOption: LiveData<Boolean> = Transformations.map(paymentToken) {
        !it.isNullOrEmpty()
    }

    fun setCardType(value: PaymentMethod) {
        cardType.postValue(value)
    }

    fun setBillingValue(value: BigDecimal) {
        billingValue.postValue(value)
    }

    fun setInstallments(installments: Int) {
        this.installments.postValue(installments)
    }

    fun finishClicked() {
        navigator.navigateToHome()
    }

    fun readCardData() {
        val paymentToken = this.paymentToken.value ?: throw IllegalStateException("Payment token can not be null")
        val cardType = this.cardType.value ?: throw IllegalStateException("Card type value can not be null")
        val billingValue = this.billingValue.value ?: throw IllegalStateException("Card type value can not be null")
        val installments = this.installments.value ?: throw IllegalStateException("Card type value can not be null")

        navigator.navigateToCardReader(paymentToken = paymentToken, cardType = cardType, billingValue = billingValue, installments = installments)
    }

    fun navigateToCardPayment() {
        val paymentToken = this.paymentToken.value ?: throw IllegalStateException("Payment token can not be null")
        val cardType = this.cardType.value ?: throw IllegalStateException("Card type value can not be null")
        val billingValue = this.billingValue.value ?: throw IllegalStateException("Card type value can not be null")
        val installments = this.installments.value ?: throw IllegalStateException("Card type value can not be null")

        navigator.navigateToCard(paymentToken = paymentToken, cardType = cardType, billingValue = billingValue, installments = installments)
    }

    fun getQrCodeLiveData(installments: Int, billingValue: Double): LiveData<Resource<QrCodePayment>> {
        val liveData = MediatorLiveData<Resource<QrCodePayment>>()

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
                            val resource: Resource<QrCodePayment> = result.b

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