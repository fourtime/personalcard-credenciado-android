package br.com.tln.personalcard.credenciador.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.tln.personalcard.credenciador.databinding.ItemTransactionsBinding
import br.com.tln.personalcard.credenciador.extensions.format
import br.com.tln.personalcard.credenciador.model.TransactionDailySummary
import br.com.tln.personalcard.credenciador.transformer.TextTransformer

class TransactionsAdapter(
    private val clickListener: (TransactionDailySummary) -> Unit
) : PagedListAdapter<TransactionDailySummary, TransactionsAdapter.ViewHolder>(DIFF_CONFIG) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(
        val binding: ItemTransactionsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: TransactionDailySummary

        init {
            binding.root.setOnClickListener {
                clickListener(this.item)
            }
        }

        fun bind(item: TransactionDailySummary) {
            this.item = item
            binding.itemTransactionsWeekDayMonth.text = item.date.format("dd MMM").toUpperCase()
            binding.itemTransactionsWeekDay.text = item.date.format(pattern = "EEEE").capitalize()
            binding.itemTransactionsValue.text = TextTransformer().currencyFormat(item.totalValue)
        }
    }

    companion object {
        private val DIFF_CONFIG = AsyncDifferConfig.Builder(
            object : DiffUtil.ItemCallback<TransactionDailySummary>() {
                override fun areItemsTheSame(oldItem: TransactionDailySummary, newItem: TransactionDailySummary): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: TransactionDailySummary, newItem: TransactionDailySummary): Boolean {
                    return oldItem.date == newItem.date
                }
            }
        ).build()
    }
}