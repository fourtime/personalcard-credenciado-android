package br.com.tln.personalcard.credenciador.model

import java.io.Serializable
import java.math.BigDecimal

class AnalyticSummary(
    val pageNumber: Int,
    val pageSize: Int,
    val totalRecords: Int,
    val transactions: List<Transaction>,
    val totalTicketValue: BigDecimal,
    val averageTicketValue: BigDecimal
) : Serializable