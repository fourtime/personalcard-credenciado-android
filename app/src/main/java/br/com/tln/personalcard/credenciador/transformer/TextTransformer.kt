package br.com.tln.personalcard.credenciador.transformer

import br.com.tln.personalcard.credenciador.PT_BR
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString
import java.math.BigDecimal
import java.text.NumberFormat

class TextTransformer {

    fun currencyFormat(value: BigDecimal?): String? {
        return currencyFormat(value, true)
    }

    fun currencyFormatWithoutSymbol(value: BigDecimal?): String? {
        return currencyFormat(value, false)
    }

    private fun currencyFormat(value: BigDecimal?, symbol: Boolean): String? {
        return value?.let {
            val currencyInstance = NumberFormat.getCurrencyInstance(PT_BR)

            val formattedValue = currencyInstance
                .format(it)

            if (symbol) {
                formattedValue
            } else {
                formattedValue.replace(currencyInstance.currency.symbol, "").trim()
            }
        }
    }

    fun mask(value: String?, mask: String): String? {
        return value?.let {
            Mask(mask).apply(
                text = CaretString(value, value.length),
                autocomplete = false
            ).formattedText.string
        }
    }
}