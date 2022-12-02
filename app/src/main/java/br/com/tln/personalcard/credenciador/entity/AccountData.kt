package br.com.tln.personalcard.credenciador.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "account")
@TypeConverters(DbTypeConverters::class)
data class AccountData(
    @PrimaryKey(autoGenerate = false) val id: Int = ID,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "local_password") val localPassword: String? = null,
    @ColumnInfo(name = "locked") val locked: Boolean = false
) {

    companion object {
        const val ID: Int = 1
    }
}