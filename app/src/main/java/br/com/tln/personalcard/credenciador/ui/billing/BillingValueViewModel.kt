package br.com.tln.personalcard.credenciador.ui.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.extensions.isValueEqual
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import java.math.BigDecimal
import javax.inject.Inject

class BillingValueViewModel @Inject constructor(
    sessionRepository: SessionRepository,
    navigator: BillingValueNavigator
) : SessionRequiredBaseViewModel<BillingValueNavigator>(
    sessionRepository = sessionRepository,
    navigator = navigator
) {

    var cardType = MutableLiveData<PaymentMethod>()
    var maxInstallments = MutableLiveData<Int>()
    val value = MutableLiveData<BigDecimal>().also { it.postValue(BigDecimal.ZERO) }

    val valid: LiveData<Boolean> = Transformations.map(value) {
        it != null && !it.isValueEqual(BigDecimal.ZERO)
    }

    fun setCardType(value: PaymentMethod) {
        this.cardType.postValue(value)
    }

    fun setMaxInstallments(maxInstallments: Int) {
        this.maxInstallments.postValue(maxInstallments)
    }

    fun continueClicked() {
        val value = value.value ?: return

        val maxInstallments = this.maxInstallments.value ?: throw IllegalStateException("Max installments can not be null")
        val cardType = this.cardType.value ?: throw IllegalStateException("Card type can not be null")

        if (maxInstallments > 1) {
            navigator.navigateToChooseInstallments(
                cardType = cardType,
                billingValue = value,
                maxInstallments = maxInstallments
            )
        } else {
            navigator.navigateToQrCode(cardType = cardType, billingValue = value, installments = 1)
        }
    }
}