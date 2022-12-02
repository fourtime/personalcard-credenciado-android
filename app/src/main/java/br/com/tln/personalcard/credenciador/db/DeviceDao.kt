package br.com.tln.personalcard.credenciador.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.tln.personalcard.credenciador.entity.Device

@Dao
abstract class DeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(device: Device)

    @Update(onConflict = OnConflictStrategy.ABORT)
    abstract fun update(device: Device)

    @Query("SELECT * FROM device WHERE id = ${Device.ID}")
    abstract fun get(): Device?

    @Query("SELECT * FROM device WHERE id = ${Device.ID}")
    abstract fun getLiveData(): LiveData<Device>
}