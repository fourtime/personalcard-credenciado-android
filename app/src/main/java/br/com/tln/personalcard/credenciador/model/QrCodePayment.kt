package br.com.tln.personalcard.credenciador.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import java.io.Serializable
import java.math.BigDecimal

@Keep
class QrCodePayment(
        val paymentToken: String?,
        val qrCode: String
) : Serializable