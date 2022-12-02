package br.com.tln.personalcard.credenciador.model

import java.math.BigDecimal

class TransactionPeriodSummary(
    val period: String,
    val totalValue: BigDecimal,
    val averageTicketValue: BigDecimal,
    val dailySummaryList: List<TransactionDailySummary>
)