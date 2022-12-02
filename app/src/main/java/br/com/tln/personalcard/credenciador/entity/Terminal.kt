package br.com.tln.personalcard.credenciador.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.LocalDateTime

@Entity(tableName = "terminal")
@TypeConverters(DbTypeConverters::class)
data class Terminal(
    @PrimaryKey(autoGenerate = false) val id: Int = ID,
    @ColumnInfo(name = "accreditedCode") val accreditedCode: String,
    @ColumnInfo(name = "cnpj") val cnpj: String,
    @ColumnInfo(name = "businessName") val businessName: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "neighborhood") val neighborhood: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "postalCode") val postalCode: String,
    @ColumnInfo(name = "maxInstallment") val maxInstallment: Int,
    @ColumnInfo(name = "supportUrl") val supportUrl: String,
    @ColumnInfo(name = "acceptedPaymentMethods") val acceptedPaymentMethods: List<PaymentMethod>,
    @ColumnInfo(name = "fetchTime") val fetchTime: LocalDateTime
) {

    companion object {
        const val ID: Int = 1
    }
}