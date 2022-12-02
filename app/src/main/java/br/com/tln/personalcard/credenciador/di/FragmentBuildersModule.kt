package br.com.tln.personalcard.credenciador.di

import br.com.tln.personalcard.credenciador.ui.billing.BillingInstallmentsFragment
import br.com.tln.personalcard.credenciador.ui.billing.BillingQrCodeFragment
import br.com.tln.personalcard.credenciador.ui.billing.BillingValueFragment
import br.com.tln.personalcard.credenciador.ui.changepassword.ChangePasswordFragment
import br.com.tln.personalcard.credenciador.ui.home.HomeFragment
import br.com.tln.personalcard.credenciador.ui.initialization.InitializationCreatePasswordFragment
import br.com.tln.personalcard.credenciador.ui.initialization.InitializationLoginFragment
import br.com.tln.personalcard.credenciador.ui.transactions.DayTransactionsFragment
import br.com.tln.personalcard.credenciador.ui.transactions.TransactionsFragment
import br.com.tln.personalcard.credenciador.ui.unlock.UnlockSessionFragment
import br.com.tln.personalcard.credenciador.ui.welcome.WelcomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeWelcomeFragment(): WelcomeFragment

    @ContributesAndroidInjector
    abstract fun contributeInitializationLoginFragment(): InitializationLoginFragment

    @ContributesAndroidInjector
    abstract fun contributeInitializationCreatePasswordFragment(): InitializationCreatePasswordFragment

    @ContributesAndroidInjector
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector
    abstract fun contributeUnlockSessionFragment(): UnlockSessionFragment

    @ContributesAndroidInjector
    abstract fun contributeBillingValueFragment(): BillingValueFragment

    @ContributesAndroidInjector
    abstract fun contributeBillingInstallmentsFragment(): BillingInstallmentsFragment

    @ContributesAndroidInjector
    abstract fun contributeBillingQrCodeFragment(): BillingQrCodeFragment

    @ContributesAndroidInjector
    abstract fun contributeTransactionsFragment(): TransactionsFragment

    @ContributesAndroidInjector
    abstract fun contributeDayTransactionsFragment(): DayTransactionsFragment
}