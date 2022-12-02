package br.com.tln.personalcard.credenciador.di

import android.app.Application
import androidx.room.Room
import br.com.tln.personalcard.credenciador.APP_DB
import br.com.tln.personalcard.credenciador.SESSION_DB
import br.com.tln.personalcard.credenciador.db.AccessTokenDao
import br.com.tln.personalcard.credenciador.db.AccountDataDao
import br.com.tln.personalcard.credenciador.db.AppDb
import br.com.tln.personalcard.credenciador.db.DeviceDao
import br.com.tln.personalcard.credenciador.db.FleetProductDao
import br.com.tln.personalcard.credenciador.db.SessionDb
import br.com.tln.personalcard.credenciador.db.TerminalDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("unused")
@Module
class DbModule {

    @Provides
    @Singleton
    fun provideSessionDb(application: Application): SessionDb {
        return Room.databaseBuilder(application, SessionDb::class.java, SESSION_DB)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDb(application: Application): AppDb {
        return Room.databaseBuilder(application, AppDb::class.java, APP_DB)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideAccountDao(sessionDb: SessionDb): AccountDataDao {
        return sessionDb.accountDataDao()
    }

    @Provides
    @Singleton
    fun provideAccessToken(sessionDb: SessionDb): AccessTokenDao {
        return sessionDb.accessTokenDao()
    }

    @Provides
    @Singleton
    fun provideTerminalDao(sessionDb: SessionDb): TerminalDao {
        return sessionDb.terminalDao()
    }

    @Provides
    @Singleton
    fun provideDeviceDao(sessionDb: SessionDb): DeviceDao {
        return sessionDb.deviceDao()
    }

    @Provides
    @Singleton
    fun provideFleetProductDao(appDb: AppDb): FleetProductDao {
        return appDb.fleetProductDao()
    }
}