package br.com.tln.personalcard.credenciador.ui.billing

import android.util.Base64
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import arrow.core.Either
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.binding.ConditionalMask
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.model.CardPayment
import br.com.tln.personalcard.credenciador.provider.ResourceProvider
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import br.com.tln.personalcard.credenciador.repository.TerminalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.math.BigDecimal
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class BillingPaymentSuccessViewModel @Inject constructor(
        sessionRepository: SessionRepository,
        navigator: BillingPaymentSuccessNavigator
) : SessionRequiredBaseViewModel<BillingPaymentSuccessNavigator>(
        sessionRepository = sessionRepository,
        navigator = navigator
) {

    private val _establishmentName = MutableLiveData<String>()
    private val _transactionValue = MutableLiveData<BigDecimal>()
    private val _transactionDate = MutableLiveData<LocalDate>()
    private val _transactionTime = MutableLiveData<LocalTime>()
    private val _authorizationNumber = MutableLiveData<String>()
    private val _transactionNumber = MutableLiveData<String>()

    val establishmentName: LiveData<String> get() = _establishmentName
    val transactionValue: LiveData<BigDecimal> get() = _transactionValue
    val transactionDate: LiveData<LocalDate> get() = _transactionDate
    val transactionTime: LiveData<LocalTime> get() = _transactionTime
    val authorizationNumber: LiveData<String> get() = _authorizationNumber
    val transactionNumber: LiveData<String> get() = _transactionNumber

    fun setPaymentData(paymentData: CardPayment, billingValue: String) {
        _establishmentName.value = paymentData.bearerName
        _transactionValue.value = billingValue.toBigDecimal()
        _transactionDate.value = paymentData.transactionDate.toLocalDate()
        _transactionTime.value = paymentData.transactionDate.toLocalTime()
        _authorizationNumber.value = paymentData.nsuAuthorization.toString()
        _transactionNumber.value = paymentData.nsuHost.toString()
    }

    fun finishClicked() {
        navigator.navigateToHome()
    }
}