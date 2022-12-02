package br.com.tln.personalcard.credenciador.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.LocalDateTime

@Entity(tableName = "access_token")
@TypeConverters(DbTypeConverters::class)
data class AccessToken(
    @PrimaryKey(autoGenerate = false) val id: Int = ID,
    @ColumnInfo(name = "token_type") val type: String,
    @ColumnInfo(name = "access_token") val value: String,
    @ColumnInfo(name = "scope") val scope: String,
    @ColumnInfo(name = "expiration") val expiration: LocalDateTime
) {
    val formattedToken
        get() = "$type $value"

    companion object {
        const val ID: Int = 1
    }
}