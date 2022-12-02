package br.com.tln.personalcard.credenciador.extensions

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

fun LocalDate.format(pattern: String = "yyyy-MM-dd"): String {
    return DateTimeFormatter.ofPattern(pattern).format(this)
}

fun LocalDateTime.format(pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): String {
    return DateTimeFormatter.ofPattern(pattern).format(this)
}
