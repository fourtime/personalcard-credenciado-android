package br.com.tln.personalcard.credenciador.ui.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.databinding.ItemDayTransactionsBinding
import br.com.tln.personalcard.credenciador.extensions.format
import br.com.tln.personalcard.credenciador.model.Transaction
import br.com.tln.personalcard.credenciador.transformer.TextTransformer

class DayTransactionsAdapter(
    private val cancelClickListener: (Transaction) -> Unit
) : PagedListAdapter<Transaction, DayTransactionsAdapter.ViewHolder>(DIFF_CONFIG) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDayTransactionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it)}
    }

    fun changeItem(transaction: Transaction) {
//        val index = this.items.indexOf(transaction)
//
//        if (index >= 0) {
//            notifyItemChanged(index)
//        }
    }

    fun removeItem(transaction: Transaction) {
//        val index = this.items.indexOf(transaction)
//
//        if (index >= 0) {
//            this.items.removeAt(index)
//            notifyItemRemoved(index)
//        }
    }

    inner class ViewHolder(val binding: ItemDayTransactionsBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: Transaction

        init {
            binding.itemDayTransactionCancel.setOnClickListener {
                cancelClickListener(this.item)
            }
        }

        fun bind(item: Transaction) {
            this.item = item

            if (adapterPosition == itemCount - 1) {
                binding.divider.visibility = View.GONE
            } else {
                binding.divider.visibility = View.VISIBLE
            }

            if (item.canceling) {
                binding.itemDayTransactionCancel.visibility = View.INVISIBLE
                binding.itemDayTransactionCancelProgress.visibility = View.VISIBLE
            } else {
                binding.itemDayTransactionCancelProgress.visibility = View.GONE
                binding.itemDayTransactionCancel.visibility = View.VISIBLE

            }

            var typeAndLastDigits: String = when {
                item.cardType == 0 -> {
                    itemView.context.getString(R.string.day_transactions_card_type_post_paid, item.cardLastDigits)
                }
                item.cardType == 1 -> {
                    itemView.context.getString(R.string.day_transactions_card_type_prepaid, item.cardLastDigits)
                }
                item.cardType == 2 -> {
                    itemView.context.getString(R.string.day_transactions_card_type_frota, item.cardLastDigits)
                }
                else -> {
                    itemView.context.getString(R.string.day_transactions_card_type_unknown, item.cardLastDigits)
                }
            }

            binding.itemDayTransactionsNsuAuthorization.text = item.nsuAuthorization
            binding.itemDayTransactionsValue.text = TextTransformer().currencyFormat(item.value)
            binding.itemDayTransactionsCardLastDigits.text = typeAndLastDigits
            binding.itemDayTransactionsHour.text = item.transactionTimestamp.format(pattern = "HH:mm")
        }
    }

    companion object {
        private val DIFF_CONFIG = AsyncDifferConfig.Builder(
            object : DiffUtil.ItemCallback<Transaction>() {
                override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                    return oldItem.nsuAuthorization == newItem.nsuAuthorization && oldItem.nsuHost == newItem.nsuHost
                }
            }
        ).build()
    }
}