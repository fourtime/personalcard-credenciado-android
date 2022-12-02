package br.com.tln.personalcard.credenciador.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.tln.personalcard.credenciador.entity.FleetProduct

@Dao
abstract class FleetProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(fleetProduct: FleetProduct)

    @Query("SELECT * FROM fleet_product WHERE id = :id")
    abstract fun findById(id: Int): LiveData<FleetProduct>
}