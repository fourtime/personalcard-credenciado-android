package br.com.tln.personalcard.credenciador.extensions

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

fun CharSequence.toLocalDate(pattern: String = "yyyy-MM-dd"): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))
}

fun CharSequence.toLocalDateTime(pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))
}
