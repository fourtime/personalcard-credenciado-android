package br.com.tln.personalcard.credenciador.entity

import androidx.room.TypeConverter
import br.com.tln.personalcard.credenciador.extensions.format
import br.com.tln.personalcard.credenciador.extensions.toLocalDateTime
import org.threeten.bp.LocalDateTime

object DbTypeConverters {
    @JvmStatic
    @TypeConverter
    fun typeToId(type: FleetProduct.Type): Int? {
        return type.id
    }

    @JvmStatic
    @TypeConverter
    fun idToType(id: Int?): FleetProduct.Type {
        return FleetProduct.Type.fromId(id)
    }

    @JvmStatic
    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? {
        return value?.toLocalDateTime()
    }

    @JvmStatic
    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime?): String? {
        return value?.format()
    }

    @JvmStatic
    @TypeConverter
    fun stringToPaymentMethods(value: String?): List<PaymentMethod>? {
        return value?.split(",")
            ?.map {
                PaymentMethod.fromId(it.toInt())
            }?.filterNotNull()
            ?.toList()
    }

    @JvmStatic
    @TypeConverter
    fun paymentMethodsToString(value: List<PaymentMethod>?): String? {
        return value?.map {
            it.id.toString()
        }?.joinToString(separator = ",")
    }
}