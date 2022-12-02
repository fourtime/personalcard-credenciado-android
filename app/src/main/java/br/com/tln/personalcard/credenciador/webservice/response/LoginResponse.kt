package br.com.tln.personalcard.credenciador.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "scope") val scope: String,
    @Json(name = "token_Type") val type: String,
    @Json(name = "access_Token") val accessToken: String,
    @Json(name = "expires_In") val expiresIn: Long
)