package br.com.tln.personalcard.credenciador.db

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.tln.personalcard.credenciador.entity.AccessToken
import br.com.tln.personalcard.credenciador.entity.AccountData
import br.com.tln.personalcard.credenciador.entity.Device
import br.com.tln.personalcard.credenciador.entity.FleetProduct
import br.com.tln.personalcard.credenciador.entity.Terminal

@Database(
    entities = [AccountData::class, AccessToken::class, Terminal::class, Device::class],
    version = 1,
    exportSchema = false
)
abstract class SessionDb : RoomDatabase() {
    abstract fun accountDataDao(): AccountDataDao
    abstract fun accessTokenDao(): AccessTokenDao
    abstract fun terminalDao(): TerminalDao
    abstract fun deviceDao(): DeviceDao
}

@Database(
    entities = [FleetProduct::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun fleetProductDao(): FleetProductDao
}