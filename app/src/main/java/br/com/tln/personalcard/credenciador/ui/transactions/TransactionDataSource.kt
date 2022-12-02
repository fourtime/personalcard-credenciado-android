package br.com.tln.personalcard.credenciador.ui.transactions

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import br.com.tln.personalcard.credenciador.model.TransactionDailySummary
import org.threeten.bp.LocalDateTime

class TransactionDataSource(
    val viewModel: TransactionsViewModel,
    val from: LocalDateTime,
    val to: LocalDateTime
) : PositionalDataSource<TransactionDailySummary>() {

    companion object {
        private const val FIRST_PAGE = 1
    }

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<TransactionDailySummary>
    ) {

        viewModel.getTransactionPeriodSummary(
            from = from,
            to = to,
            pageNumber = FIRST_PAGE,
            pageSize = params.pageSize
        ) {
            callback.onResult(it.dailySummaryList, 0)
        }
    }

    override fun loadRange(
        params: LoadRangeParams,
        callback: LoadRangeCallback<TransactionDailySummary>
    ) {
        val nextPage: Int
        val startPosition = params.startPosition
        val loadSize = params.loadSize

        val hasNextPage = startPosition % loadSize == 0

        if (hasNextPage) {
            nextPage = (startPosition / loadSize) + FIRST_PAGE
        } else {
            return
        }

        viewModel.getTransactionPeriodSummary(
            from = from,
            to = to,
            pageNumber = nextPage,
            pageSize = params.loadSize
        ) {
            callback.onResult(it.dailySummaryList)
        }
    }

    class Factory(val viewModel: TransactionsViewModel, val from: LocalDateTime, val to: LocalDateTime ) : DataSource.Factory<Int, TransactionDailySummary>() {

        override fun create(): DataSource<Int, TransactionDailySummary> {
            return TransactionDataSource(viewModel = viewModel, from = from, to = to)
        }
    }

}
