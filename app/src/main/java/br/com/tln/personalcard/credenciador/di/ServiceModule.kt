package br.com.tln.personalcard.credenciador.di

import br.com.tln.personalcard.credenciador.fcm.MessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract fun contributeMessagingService(): MessagingService
}