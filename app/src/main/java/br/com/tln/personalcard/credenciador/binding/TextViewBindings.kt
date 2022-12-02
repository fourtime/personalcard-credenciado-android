package br.com.tln.personalcard.credenciador.binding

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import br.com.tln.personalcard.credenciador.R
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString


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
                text = CaretString(rawText, rawText.length),
                autocomplete = false
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
}