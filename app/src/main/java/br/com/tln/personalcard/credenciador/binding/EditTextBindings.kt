package br.com.tln.personalcard.credenciador.binding

import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import br.com.tln.personalcard.credenciador.PT_BR
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.extensions.isPositive
import br.com.tln.personalcard.credenciador.extensions.isValueEqual
import com.redmadrobot.inputmask.MaskedTextChangedListener
import timber.log.Timber
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import kotlin.math.roundToInt

object EditTextBindings {

    @JvmStatic
    @BindingAdapter(value = ["android:text", "mask", "android:textAttrChanged"], requireAll = true)
    fun EditText.mask(text: String?, mask: String, textAttrChanged: InverseBindingListener) {
        mask(
            text = text,
            mask = mask,
            maskBind = false,
            conditionalMasks = null,
            textAttrChanged = textAttrChanged
        )
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:text", "mask", "maskBind", "android:textAttrChanged"],
        requireAll = true
    )
    fun EditText.mask(
        text: String?,
        mask: String,
        maskBind: Boolean,
        textAttrChanged: InverseBindingListener
    ) {
        mask(
            text = text,
            mask = mask,
            maskBind = maskBind,
            conditionalMasks = null,
            textAttrChanged = textAttrChanged
        )
    }

    @JvmStatic
    @BindingAdapter(value = ["android:text", "mask", "android:textAttrChanged"], requireAll = true)
    fun EditText.mask(
        text: String?,
        conditionalMasks: List<ConditionalMask>,
        textAttrChanged: InverseBindingListener
    ) {
        mask(
            text = text,
            mask = conditionalMasks.firstOrNull()?.mask
                ?: throw IllegalStateException("Conditional masks must not be empty"),
            maskBind = false,
            conditionalMasks = conditionalMasks,
            textAttrChanged = textAttrChanged
        )
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:text", "mask", "maskBind", "android:textAttrChanged"],
        requireAll = true
    )
    fun EditText.mask(
        text: String?,
        conditionalMasks: List<ConditionalMask>,
        maskBind: Boolean,
        textAttrChanged: InverseBindingListener
    ) {
        mask(
            text = text,
            mask = conditionalMasks.firstOrNull()?.mask
                ?: throw IllegalStateException("Conditional masks must not be empty"),
            maskBind = maskBind,
            conditionalMasks = conditionalMasks,
            textAttrChanged = textAttrChanged
        )
    }

    private fun EditText.mask(
        text: String?,
        mask: String,
        maskBind: Boolean,
        conditionalMasks: List<ConditionalMask>?,
        textAttrChanged: InverseBindingListener
    ) {

        val currentMask = getTag(R.id.editTextMask) as? String?
        val currentConditionalMasks =
            getTag(R.id.editTextConditionalMasks) as? List<ConditionalMask>
        var currentMaskListener = getTag(R.id.editTextMaskListener) as? MaskedTextChangedListener?

        val applyMask = currentMaskListener == null || maskChanged(
            conditionalMasks = conditionalMasks,
            currentConditionalMasks = currentConditionalMasks,
            mask = mask,
            currentMask = currentMask
        )

        if (applyMask) {
            if (currentMaskListener != null) {
                this.removeTextChangedListener(currentMaskListener)
                this.onFocusChangeListener = null
            }

            if (conditionalMasks == null) {
                setTag(R.id.editTextConditionalMask, null)
                setTag(R.id.editTextConditionalMasks, null)
            } else {
                val conditionalMask = conditionalMasks.firstOrNull { conditionalMask ->
                    conditionalMask.mask == mask
                }
                    ?: throw IllegalStateException("Mask $mask is not present in the given conditional masks")

                setTag(R.id.editTextConditionalMask, conditionalMask)
                setTag(R.id.editTextConditionalMasks, conditionalMasks)
            }

            val maskListener = MaskedTextChangedListener(
                format = mask,
                field = this
            )
            currentMaskListener = maskListener

            var changingMask = false

            maskListener.valueListener =
                object : MaskedTextChangedListener.ValueListener, TextWatcher {
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (maskBind) {
                            setTag(R.id.editTextMaskMaskedText, s.toString())
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                        if (!maskBind) {
                            setTag(R.id.editTextMaskRawText, extractedValue)
                        }
                        textAttrChanged.onChange()

                        if (conditionalMasks == null || changingMask) {
                            return
                        }

                        val currentConditionalMask =
                            getTag(R.id.editTextConditionalMask) as? ConditionalMask? ?: return

                        if (currentConditionalMask.applyTo(extractedValue)) {
                            return
                        }

                        val newConditionalMask = conditionalMasks.firstOrNull { mask ->
                            mask.applyTo(extractedValue)
                        } ?: run {
                            Timber.w("Could not find a suitable mask")
                            return
                        }

                        changingMask = true

                        val newMask = newConditionalMask.mask

                        setTag(R.id.editTextMask, newMask)
                        setTag(R.id.editTextConditionalMask, newConditionalMask)

                        maskListener.primaryFormat = newMask
                        maskListener.setText(extractedValue)

                        changingMask = false
                    }
                }

            setTag(R.id.editTextMask, mask)
            setTag(R.id.editTextMaskListener, maskListener)

            addTextChangedListener(maskListener)
            onFocusChangeListener = maskListener
        }

        val oldText = getTextString()

        if (text === oldText || text == null && oldText.isEmpty()) {
            return
        }

        if (text == oldText) {
            return
        }

        val finalText = text ?: ""
        currentMaskListener?.run {
            setText(finalText)
        } ?: this.setText(finalText)
    }

    private fun maskChanged(
        conditionalMasks: List<ConditionalMask>?,
        currentConditionalMasks: List<ConditionalMask>?,
        mask: String,
        currentMask: String?
    ): Boolean {
        return if (conditionalMasks != null || currentConditionalMasks != null) {
            conditionalMasks != currentConditionalMasks
        } else {
            mask != currentMask
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    fun EditText.getTextString(): String {
        return getTag(R.id.editTextMaskMaskedText) as? String
            ?: getTag(R.id.editTextMaskRawText) as? String?
            ?: text.toString()
    }
    @JvmStatic
    @BindingAdapter(value = ["android:text", "symbolTextSize", "showSymbol"], requireAll = true)
    fun EditText.formatCurrency(newValue: BigDecimal?, symbolTextSize: Float, showSymbol: Boolean) {
        val initialized = getTag(R.id.textViewCurrencyValueInitialized) as? Boolean ?: false
        val currentValue = getTag(R.id.textViewCurrencyValue) as? BigDecimal

        if (initialized && !newValue.isValueEqual(BigDecimal.ZERO) && newValue.isValueEqual(currentValue)) {
            return
        }

        val currencyInstance: NumberFormat = if (showSymbol) {
            NumberFormat.getCurrencyInstance(PT_BR)
        } else {
            val nf = NumberFormat.getCurrencyInstance(PT_BR)
            val decimalFormatSymbols: DecimalFormatSymbols = (nf as DecimalFormat).decimalFormatSymbols
            decimalFormatSymbols.currencySymbol = ""
            nf.decimalFormatSymbols = decimalFormatSymbols
            nf
        }

        val text: CharSequence = if (newValue == null || newValue.isValueEqual(BigDecimal.ZERO)) {
            val currencyPrefix = if (currencyInstance is DecimalFormat) {
                currencyInstance.positivePrefix
            } else {
                currencyInstance.currency.symbol
            }

            val formattedValue = currencyInstance.format("0.00".toBigDecimal())

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
        } else {
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

        val oldText = this.text.toString()

        if (text.toString() == oldText) {
            return
        }

        if (!initialized) {
            setTag(R.id.textViewCurrencyValueInitialized, true)
        }

        setTag(R.id.textViewCurrencyValue, newValue)
        this.setText(text)
        this.setSelection(this.text.length)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text", event="android:textAttrChanged")
    fun EditText.getTextAsBigDecimal(): BigDecimal? {

        return this.text.replace(Regex("[^0-9]"), "").padStart(3, '0').let {
            "${it.substring(0, it.length - 2)}.${it.substring(it.length - 2)}".toBigDecimal()
        }
    }
}

class ConditionalMask(val mask: String, val conditionValidator: (text: String?) -> Boolean) {
    fun applyTo(text: String?): Boolean {
        return conditionValidator(text)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConditionalMask

        if (mask != other.mask) return false

        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}