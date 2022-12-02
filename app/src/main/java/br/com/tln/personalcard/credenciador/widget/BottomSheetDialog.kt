package br.com.tln.personalcard.credenciador.widget

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.databinding.WidgetBottomSheetDialogBinding

class BottomSheetDialog : com.google.android.material.bottomsheet.BottomSheetDialog {

    val binding: WidgetBottomSheetDialogBinding

    constructor(context: Context) : super(context, R.style.AppTheme_Dialog)
    constructor(context: Context, theme: Int) : super(context, theme)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(
        context,
        cancelable,
        cancelListener
    )

    init {
        setContentView(R.layout.widget_bottom_sheet_dialog)
        binding = WidgetBottomSheetDialogBinding.bind(findViewById(R.id.bottom_sheet_dialog_container)!!)

        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showCloseButton(show: Boolean, clickListener: ((BottomSheetDialog) -> Unit) = { _ -> dismiss() }) {
        binding.closeButtonVisible = show
        if (show) {
            binding.closeButtonClickListener = View.OnClickListener { clickListener(this) }
        }
    }

    fun icon(@DrawableRes res: Int) {
        binding.iconDrawable = ContextCompat.getDrawable(context, res)
    }

    fun title(title: CharSequence) {
        binding.title = title
    }

    fun message(message: CharSequence) {
        binding.message = message
    }

    fun input(hasInputField: Boolean){
        binding.hasInputField = hasInputField
    }

    fun input(): CharSequence?{
        return binding.input
    }

    fun neutralText(neutralText: CharSequence, clickListener: ((BottomSheetDialog) -> Unit) = { _ -> dismiss() }) {
        binding.neutralText = neutralText
        binding.neutralClickListener = View.OnClickListener { clickListener(this) }
    }

    fun confirmText(confirmText: CharSequence, clickListener: ((BottomSheetDialog) -> Unit) = { _ -> dismiss() }) {
        binding.confirmText = confirmText
        binding.confirmClickListener = View.OnClickListener { clickListener(this) }
    }

    fun dangerText(dangerText: CharSequence, clickListener: ((BottomSheetDialog) -> Unit) = { _ -> dismiss() }) {
        binding.dangerText = dangerText
        binding.dangerClickListener = View.OnClickListener { clickListener(this) }
    }

    inline fun show(func: BottomSheetDialog.() -> Unit): BottomSheetDialog {
        this.func()
        this.show()
        return this
    }
}