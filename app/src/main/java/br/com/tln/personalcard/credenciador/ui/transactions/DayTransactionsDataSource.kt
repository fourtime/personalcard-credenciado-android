package br.com.tln.personalcard.credenciador.ui.transactions

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import br.com.tln.personalcard.credenciador.model.Transaction
import org.threeten.bp.LocalDateTime

class DayTransactionsDataSource(
    val period: LocalDateTime,
    val viewModel: DayTransactionsViewModel
) : PositionalDataSource<Transaction>() {

    companion object {
        private const val FIRST_PAGE = 1
    }

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<Transaction>
    ) {

        viewModel.getTransactionDay(
            from = period,
            to = period,
            pageNumber = FIRST_PAGE,
            pageSize = params.pageSize
        ) {
            callback.onResult(it.transactions, 0)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Transaction>) {
        val nextPage: Int
        val startPosition = params.startPosition
        val loadSize = params.loadSize

        val hasNextPage = startPosition % loadSize == 0

        if (hasNextPage) {
            nextPage = (startPosition / loadSize) + FIRST_PAGE
        } else {
            return
        }
        viewModel.getTransactionDay(
            from = period,
            to = period,
            pageNumber = nextPage,
            pageSize = params.loadSize
        ) {
            callback.onResult(it.transactions)
        }
    }

    class Factory(val period: LocalDateTime, val viewModel: DayTransactionsViewModel) :
        DataSource.Factory<Int, Transaction>() {

        override fun create(): DataSource<Int, Transaction> {
            return DayTransactionsDataSource(period = period, viewModel = viewModel)
        }
    }

}
