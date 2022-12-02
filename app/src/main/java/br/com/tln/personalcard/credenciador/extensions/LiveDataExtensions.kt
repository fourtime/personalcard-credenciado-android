package br.com.tln.personalcard.credenciador.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import br.com.tln.personalcard.credenciador.core.Event

fun <T> LiveData<T>.observe(owner: LifecycleOwner, body: (T) -> Unit) {
    observe(
        liveData = this,
        owner = owner,
        allowNullable = false,
        body = body
    )
}

fun <T> LiveData<T>.observeNullable(owner: LifecycleOwner, body: (T) -> Unit) {
    observe(
        liveData = this,
        owner = owner,
        allowNullable = true,
        body = body
    )
}

private fun <T> observe(liveData: LiveData<T>, owner: LifecycleOwner, allowNullable: Boolean, body: (T) -> Unit) {
    val observer: Observer<T> = if (!allowNullable) {
        Observer {
            if (it == null) {
                return@Observer
            }

            body(it)
        }
    } else {
        Observer(body)
    }

    liveData.observe(owner, observer)
}

fun <T> LiveData<out Event<T>>.observeEvent(owner: LifecycleOwner, body: (Event<T>) -> Unit) {
    observe(owner) { event ->
        if (event.handled) {
            return@observe
        }

        event.handled = true

        body(event)
    }
}