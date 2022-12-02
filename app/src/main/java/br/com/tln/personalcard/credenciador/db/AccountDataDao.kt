package br.com.tln.personalcard.credenciador.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.tln.personalcard.credenciador.entity.AccountData

@Dao
abstract class AccountDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(account: AccountData)

    @Update(onConflict = OnConflictStrategy.ABORT)
    abstract fun update(account: AccountData)

    @Query("SELECT * FROM account WHERE id = ${AccountData.ID}")
    abstract fun get(): AccountData?

    @Query("SELECT * FROM account WHERE id = ${AccountData.ID}")
    abstract fun getLiveData(): LiveData<AccountData>

    @Query("DELETE FROM account")
    abstract fun delete()
}