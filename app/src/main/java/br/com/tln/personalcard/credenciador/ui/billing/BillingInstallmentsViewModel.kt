package br.com.tln.personalcard.credenciador.ui.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import java.math.BigDecimal
import javax.inject.Inject

class BillingInstallmentsViewModel @Inject constructor(
    sessionRepository: SessionRepository,
    navigator: BillingInstallmentsNavigator
) : SessionRequiredBaseViewModel<BillingInstallmentsNavigator>(
    sessionRepository = sessionRepository,
    navigator = navigator
) {

    var mBillingValue: BigDecimal? = null
    var mMaxInstallments: Int? = null

    val cardType = MutableLiveData<PaymentMethod>()
    val billingValue = MutableLiveData<BigDecimal>()
    val maxInstallments = MutableLiveData<String>()
    val value = MutableLiveData<String?>()
    val valid: LiveData<Boolean> = Transformations.map(value) { value ->
        if (value.isNullOrEmpty() || maxInstallments.value.isNullOrEmpty()) {
            return@map false
        } else {
            val intValue = value.toIntOrNull() ?: return@map false

            val maxInstallments = maxInstallments.value ?: return@map false

            val maxInstallmentsInt = maxInstallments.toIntOrNull() ?: return@map false

            return@map intValue in 1..maxInstallmentsInt
        }
    }

    fun setCardType(value: PaymentMethod) {
        cardType.postValue(value)
    }

    fun setBillingValue(value: BigDecimal) {
        this.mBillingValue = value
        billingValue.postValue(value)
    }

    fun setMaxInstallments(value: Int) {
        mMaxInstallments = value
        maxInstallments.postValue(value.toString())
    }

    fun continueClicked() {
        val valueStr = value.value

        if (valueStr.isNullOrEmpty()) {
            return
        }

        val cardTypeValue = cardType.value ?: throw IllegalStateException("Billing value can not be null")
        val billingValue = mBillingValue ?: throw IllegalStateException("Billing value can not be null")

        val installments = valueStr.toInt()

        navigator.navigateToQrCode(cardType = cardTypeValue, billingValue = billingValue, installments = installments)
    }
}