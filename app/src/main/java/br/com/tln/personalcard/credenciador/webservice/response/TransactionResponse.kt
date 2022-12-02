package br.com.tln.personalcard.credenciador.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class TransactionResponse(
    @Json(name = "numeroPagina") val pageNumber: Int,
    @Json(name = "tamanhoPagina") val pageSize: Int,
    @Json(name = "totalRegistros") val totalRecords: Int,
    @Json(name = "dados") val transactions: List<TransactionListResponse>,
    @Json(name = "valorTotalPeriodo") val totalTicketValue: BigDecimal,
    @Json(name = "valorTicketMedio") val averageTicketValue: BigDecimal
)