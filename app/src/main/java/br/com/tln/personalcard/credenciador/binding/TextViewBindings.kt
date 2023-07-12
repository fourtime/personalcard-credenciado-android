package br.com.tln.personalcard.credenciador.binding

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import br.com.tln.personalcard.credenciador.PT_BR
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.extensions.format
import br.com.tln.personalcard.credenciador.extensions.isPositive
import br.com.tln.personalcard.credenciador.extensions.isValueEqual
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString
import org.threeten.bp.temporal.TemporalAccessor
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.roundToInt


object TextViewBindings {

    @JvmStatic
    @BindingAdapter(
        value = ["android:text", "emphasisText", "emphasisTextColor", "emphasisTextUnderline"],
        requireAll = true
    )
    fun TextView.emphasis(
        text: String,
        emphasisText: String,
        @ColorInt emphasisTextColor: Int,
        emphasisTextUnderline: Boolean
    ) {
        val startIndex = text.indexOf(emphasisText)
        val endIndex = text.indexOf(emphasisText) + emphasisText.length

        if (startIndex < 0 && endIndex > text.length) {
            return
        }

        val spannable = SpannableString(text)

        spannable.setSpan(
            ForegroundColorSpan(emphasisTextColor),
            startIndex,
            endIndex,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        if (emphasisTextUnderline) {
            spannable.setSpan(
                UnderlineSpan(),
                startIndex,
                endIndex,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        this.text = spannable
    }

    @JvmStatic
    @BindingAdapter(value = ["android:text", "mask"], requireAll = true)
    fun TextView.mask(rawText: String?, mask: String) {
        val currentMask = getTag(R.id.textViewMask) as? String?
        val originalText = getTag(R.id.textViewMaskRawText) as? String?
        if (originalText == rawText && currentMask == mask) {
            return
        }
        val text = rawText?.let {
            Mask(mask).apply(
                text = CaretString(rawText, rawText.length, caretGravity = CaretString.CaretGravity.FORWARD(autocompleteValue = false))
            ).formattedText.string
        }
        setTag(R.id.textViewMask, mask)
        val oldText = this.text
        if (text === oldText || text == null && oldText.isEmpty()) {
            return
        }
        if (text == oldText) {
            return
        }
        setTag(R.id.textViewMaskRawText, rawText)
        this.text = text
    }

    @JvmStatic
    @BindingAdapter(value = ["android:text"], requireAll = true)
    fun TextView.formatCurrency(value: BigDecimal?) {
        formatCurrency(newValue = value, symbolTextSize = textSize)
    }

    @JvmStatic
    @BindingAdapter(value = ["android:text", "symbolTextSize"], requireAll = true)
    fun TextView.formatCurrency(newValue: BigDecimal?, symbolTextSize: Float) {
        val initialized = getTag(R.id.textViewCurrencyValueInitialized) as? Boolean ?: false
        val currentValue = getTag(R.id.textViewCurrencyValue) as? BigDecimal

        if (initialized && newValue.isValueEqual(currentValue)) {
            return
        }

        val text: CharSequence = if (newValue == null || newValue.isValueEqual(BigDecimal.ZERO)) {
            "-"
        } else {
            val currencyInstance = NumberFormat.getCurrencyInstance(PT_BR)
            val currencyPrefix = if (currencyInstance is DecimalFormat) {
                if (newValue.isPositive()) {
                    currencyInstance.positivePrefix
                } else {
                    currencyInstance.negativePrefix
                }
            } else {
                currencyInstance.currency.symbol
            }

            val formattedValue = currencyInstance.format(newValue)

            if (symbolTextSize == textSize) {
                formattedValue
            } else {
                SpannableString(formattedValue).apply {
                    setSpan(
                            AbsoluteSizeSpan(symbolTextSize.roundToInt()),
                            formattedValue.indexOf(currencyPrefix),
                            formattedValue.indexOf(currencyPrefix) + currencyPrefix.length,
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
            }
        }

        val oldText = this.text

        if (text == oldText) {
            return
        }

        if (!initialized) {
            setTag(R.id.textViewCurrencyValueInitialized, true)
        }

        setTag(R.id.textViewCurrencyValue, newValue)
        this.text = text
    }

    @JvmStatic
    @BindingAdapter(value = ["android:text", "pattern"], requireAll = true)
    fun TextView.formatTemporalAccessor(newValue: TemporalAccessor?, pattern: String) {
        val initialized = getTag(R.id.textViewTemporalValueInitialized) as? Boolean ?: false
        val currentValue = getTag(R.id.textViewTemporalValue) as? TemporalAccessor

        if (initialized && newValue == currentValue) {
            return
        }

        val text = newValue?.format(pattern)

        val oldText = this.text

        if (text == oldText) {
            return
        }

        if (!initialized) {
            setTag(R.id.textViewTemporalValueInitialized, true)
        }

        setTag(R.id.textViewTemporalValue, newValue)
        this.text = text
    }
}