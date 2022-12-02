package br.com.tln.personalcard.credenciador.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class TerminalDataResponse(
    @Json(name = "codigoCredenciado") val accreditedCode: String,
    @Json(name = "cnpj") val cnpj: String,
    @Json(name = "razaoSocial") val businessName: String,
    @Json(name = "endereco") val address: String,
    @Json(name = "bairro") val neighborhood: String,
    @Json(name = "cidade") val city: String,
    @Json(name = "uf") val state: String,
    @Json(name = "cep") val postalCode: String,
    @Json(name = "maximoParcelas") val maxInstallment: Int
)