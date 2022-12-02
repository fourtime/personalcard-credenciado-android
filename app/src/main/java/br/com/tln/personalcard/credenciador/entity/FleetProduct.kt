package br.com.tln.personalcard.credenciador.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "fleet_product")
@TypeConverters(DbTypeConverters::class)
class FleetProduct(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "code") val code: Long?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "short_description") val shortDescription: String?,
    @ColumnInfo(name = "type") val type: FleetProduct.Type
) {

    enum class Type(val id: Int?, val description: String) {
        FUEL(1, "Combustíveis"),
        LUBRICANT(2, "Lubrificantes"),
        PRODUCT(4, "Produtos"),
        TIRE_REPAIR(5, "Borracharia"),
        WASHING(6, "Lavagem"),
        MAINTENANCE(7, "Manutenção"),
        PART_SWAP(8, "Troco"),
        OTHERS(null, "Outros");

        companion object {
            fun fromId(id: Int?): Type {
                return values().firstOrNull {
                    it.id == id
                } ?: OTHERS
            }

        }
    }
}