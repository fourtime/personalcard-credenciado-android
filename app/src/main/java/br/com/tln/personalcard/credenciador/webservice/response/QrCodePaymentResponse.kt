package br.com.tln.personalcard.credenciador.webservice.response

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class QrCodePaymentResponse(
        @Json(name = "tokenPagamento") val paymentToken: String?,
        @Json(name = "qrCodePagamento") val qrCode: String
) : Serializable