package br.com.tln.personalcard.credenciador.ui.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import arrow.core.Either
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.binding.ConditionalMask
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.model.CardPayment
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import br.com.tln.personalcard.credenciador.repository.TerminalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class BillingQrCodeReaderViewModel @Inject constructor(
    private val terminalRepository: TerminalRepository,
    sessionRepository: SessionRepository,
    navigator: BillingQrCodeReaderNavigator
) : SessionRequiredBaseViewModel<BillingQrCodeReaderNavigator>(
    sessionRepository = sessionRepository,
    navigator = navigator
) {

    private val _cardPaymentLiveData = MediatorLiveData<Resource<CardPayment>>()

    val paymentToken = MutableLiveData<String?>()
    val cardType = MutableLiveData<PaymentMethod?>()
    val billingValue = MutableLiveData<BigDecimal>()
    val installments = MutableLiveData<Int>()

    fun setPaymentToken(value: String?) { paymentToken.postValue(value) }
    fun setCardType(value: PaymentMethod) { cardType.postValue(value) }
    fun setBillingValue(value: BigDecimal) { billingValue.postValue(value) }
    fun setInstallments(value: Int) { installments.postValue(value) }

    val cardPaymentLiveData: MediatorLiveData<Resource<CardPayment>> get() = _cardPaymentLiveData

    fun navigateToCardPayment(qrcodeToken: String) {
        val paymentToken = this.paymentToken.value ?: throw IllegalStateException("Payment token can not be null")
        val cardType = this.cardType.value ?: throw IllegalStateException("Card type value can not be null")
        val billingValue = this.billingValue.value ?: throw IllegalStateException("Card type value can not be null")
        val installments = this.installments.value ?: throw IllegalStateException("Card type value can not be null")

        navigator.navigateToCardPayment(paymentToken = paymentToken, qrcodeToken = qrcodeToken, cardType = cardType, billingValue = billingValue, installments = installments)
    }
}