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
import java.math.BigDecimal
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class BillingCardPaymentViewModel @Inject constructor(
        private val terminalRepository: TerminalRepository,
        sessionRepository: SessionRepository,
        private val resourceProvider: ResourceProvider,
        navigator: BillingCardPaymentNavigator
) : SessionRequiredBaseViewModel<BillingCardPaymentNavigator>(
        sessionRepository = sessionRepository,
        navigator = navigator
) {
    private val _carPaymentLiveData = MediatorLiveData<Resource<CardPayment>>()

    val cardNumberMasks = listOf(
        ConditionalMask(resourceProvider.getString(R.string.mask_dynamic_cardnumber)) {
            it == null || it.length >= cardNumberMinLength || it.length <= cardNumberMaxLength
        }
    )
    val cardNumberMinLength by lazy { resourceProvider.getInteger(R.integer.cardnumber_min_length) }
    val cardNumberMaxLength by lazy { resourceProvider.getInteger(R.integer.cardnumber_max_length) }
    val cardPasswordMinLength by lazy { resourceProvider.getInteger(R.integer.cardpassword_min_length) }
    val cardPasswordMaxLength by lazy { resourceProvider.getInteger(R.integer.cardpassword_max_length) }

    val cardType = MutableLiveData<PaymentMethod?>()
    val paymentToken = MutableLiveData<String?>()
    val billingValue = MutableLiveData<BigDecimal>()
    val installments = MutableLiveData<Int>()
    val cardNumber = MutableLiveData<String?>()
    val cardPassword = MutableLiveData<String?>()
    val qrcodeToken = MutableLiveData<String?>()

    fun setCardType(value: PaymentMethod) { cardType.postValue(value) }
    fun setPaymentToken(value: String?) { paymentToken.postValue(value) }
    fun setBillingValue(value: BigDecimal) { billingValue.postValue(value) }
    fun setInstallments(value: Int) { installments.postValue(value) }
    fun setQrCodeToken(value: String?) { qrcodeToken.postValue(value) }

    val carPaymentLiveData: MediatorLiveData<Resource<CardPayment>> get() = _carPaymentLiveData

    val cnpj: LiveData<String?> = Transformations.map(sessionRepository.getAccountLiveData()) { it?.terminal?.cnpj }

    val valid: MutableLiveData<Boolean> by lazy {
        val liveData = MediatorLiveData<Boolean>()

        liveData.addSource(cardNumber) { liveData.postValue(isValid()) }
        liveData.addSource(cardPassword) { liveData.postValue(isValid()) }

        liveData
    }

    private fun isValid(): Boolean {
        val cardpassword = this.cardPassword.value ?: return false

        val cardnumber = this.cardNumber.value?.trim() ?: ""
        val cardToken = this.qrcodeToken.value?.trim()

        return ((cardnumber.isDigitsOnly() && cardnumber.length >= cardNumberMinLength && cardnumber.length <= cardNumberMaxLength) || (cardToken.isNullOrEmpty() == false)) && (cardpassword.isDigitsOnly() && cardpassword.length >= cardPasswordMinLength && cardpassword.length <= cardPasswordMaxLength)
    }

    val shouldShowCardField: LiveData<Boolean> = Transformations.map(qrcodeToken) {
        it.isNullOrEmpty()
    }

    private fun isQRCode(): Boolean {
        this.qrcodeToken.value?.let { return true }
        return  false
    }

    private fun getCardData(cnpj: String): String {
        val cardType = this.cardType.value?.id ?: throw IllegalStateException("Card type value can not be null")
        val paymentToken = this.paymentToken.value ?: throw IllegalStateException("Payment token value can not be null")
        val imei = this.sessionRepository.getAppPreference().getDeviceId() ?: throw IllegalStateException("IMEI value can not be null")
        val cardpassword = this.cardPassword.value ?: throw IllegalStateException("Card password value can not be null")
        val cardnumber = this.cardNumber.value ?: throw IllegalStateException("Card number value can not be null")

        val keyPart1 = imei.substring(0, 8).toLowerCase()
        val keyPart2 = cnpj.substring(0, 8).toLowerCase()
        val key = "$keyPart1$keyPart2".toByteArray()
        val vectorPart1 = paymentToken.substring(0, 8)
        val vectorPart2 = imei.substring(imei.length - 8).toUpperCase()
        val vector = "$vectorPart1$vectorPart2".toByteArray()
        var carddata = "TipoCartao=${cardType};NumeroCartao=$cardnumber;SenhaCartao=$cardpassword".toByteArray()

        val secretKey = SecretKeySpec(key, "AES")
        val iv = IvParameterSpec(vector)
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)

        val result = cipher.doFinal(carddata)
        return Base64.encodeToString(cipher.doFinal(carddata), Base64.NO_WRAP)
    }

    private fun getCardData2(qrCodeToken: String): String {
        val paymentToken = this.paymentToken.value ?: throw IllegalStateException("Payment token value can not be null")
        val imei = this.sessionRepository.getAppPreference().getDeviceId() ?: throw IllegalStateException("IMEI value can not be null")
        val cardpassword = this.cardPassword.value ?: throw IllegalStateException("Card password value can not be null")

        val keyPart1 = imei.substring(0, 8).toLowerCase()
        val keyPart2 = qrCodeToken.substring(0, 8).toLowerCase()
        val key = "$keyPart1$keyPart2".toByteArray()
        val vectorPart1 = paymentToken.substring(0, 8)
        val vectorPart2 = qrCodeToken.substring(qrCodeToken.length - 8).toUpperCase()
        val vector = "$vectorPart1$vectorPart2".toByteArray()
        var carddata = "$cardpassword".toByteArray()

        val secretKey = SecretKeySpec(key, "AES")
        val iv = IvParameterSpec(vector)
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)

        val result = cipher.doFinal(carddata)
        return Base64.encodeToString(cipher.doFinal(carddata), Base64.NO_WRAP)
    }

    fun finishClicked() {
        if (isValid() == false) { return }

        val paymentToken = this.paymentToken.value ?: throw IllegalStateException("Payment token value can not be null")

        _carPaymentLiveData.postValue(Resource.loading())

        viewModelScope.launch {

            val account = withContext(Dispatchers.IO) {  sessionRepository.getAccount() } ?: return@launch
            val terminal = withContext(Dispatchers.IO) {  sessionRepository.getTerminal() } ?: return@launch

            if (!isQRCode()) {
                val cardData = getCardData(terminal.cnpj)
                val result = terminalRepository.payWithCard(accessToken = account.accessToken, paymentToken = paymentToken, carddata = cardData)
                when (result) {
                    is Either.Left -> {
                        errorNotifier.notify(result.a)
                    }
                    is Either.Right -> {
                        val resource: Resource<CardPayment> = result.b

                        when (resource.status) {
                            Resource.Status.LOADING -> {
                                _carPaymentLiveData.postValue(Resource.loading())
                            }
                            Resource.Status.SUCCESS -> {
                                resource.data?.run {
                                    _carPaymentLiveData.postValue(Resource.success(this))
                                } ?: run {
                                    _carPaymentLiveData.postValue(resource)
                                    _unknownErrorLiveData.postValue(Event(data = null))
                                }
                            }
                            Resource.Status.ERROR -> {
                                resource.message?.let {
                                    _carPaymentLiveData.postValue(resource)
                                    _errorMessageLiveData.postValue(Event(it))
                                } ?: run {
                                    _carPaymentLiveData.postValue(resource)
                                    _unknownErrorLiveData.postValue(Event(data = null))
                                }
                            }
                        }
                    }
                }
            } else {
                val cardType = cardType.value?.id ?: throw IllegalStateException("Payment token value can not be null")
                val cardQRCode = qrcodeToken.value ?: throw IllegalStateException("QRCode token value can not be null")
                val cardData = getCardData2(cardQRCode)

                val result = terminalRepository.payWithCardQRCode(accessToken = account.accessToken, paymentToken = paymentToken, cardType = cardType, cardQRCode = cardQRCode, cardPassword = cardData)
                when (result) {
                    is Either.Left -> {
                        errorNotifier.notify(result.a)
                    }
                    is Either.Right -> {
                        val resource: Resource<CardPayment> = result.b

                        when (resource.status) {
                            Resource.Status.LOADING -> {
                                _carPaymentLiveData.postValue(Resource.loading())
                            }
                            Resource.Status.SUCCESS -> {
                                resource.data?.run {
                                    _carPaymentLiveData.postValue(Resource.success(this))
                                } ?: run {
                                    _carPaymentLiveData.postValue(resource)
                                    _unknownErrorLiveData.postValue(Event(data = null))
                                }
                            }
                            Resource.Status.ERROR -> {
                                resource.message?.let {
                                    _carPaymentLiveData.postValue(resource)
                                    _errorMessageLiveData.postValue(Event(it))
                                } ?: run {
                                    _carPaymentLiveData.postValue(resource)
                                    _unknownErrorLiveData.postValue(Event(data = null))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}