package br.com.tln.personalcard.credenciador.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.tln.personalcard.credenciador.entity.Terminal

@Dao
abstract class TerminalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(terminal: Terminal)

    @Update(onConflict = OnConflictStrategy.ABORT)
    abstract fun update(terminal: Terminal)

    @Query("SELECT * FROM terminal WHERE id = ${Terminal.ID}")
    abstract fun get(): Terminal?

    @Query("SELECT * FROM terminal WHERE id = ${Terminal.ID}")
    abstract fun getLiveData(): LiveData<Terminal>

    @Query("DELETE FROM terminal")
    abstract fun delete()
}