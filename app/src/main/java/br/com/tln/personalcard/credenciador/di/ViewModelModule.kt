/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.tln.personalcard.credenciador.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.tln.personalcard.credenciador.ui.billing.*
import br.com.tln.personalcard.credenciador.ui.changepassword.ChangePasswordViewModel
import br.com.tln.personalcard.credenciador.ui.home.HomeViewModel
import br.com.tln.personalcard.credenciador.ui.initialization.InitializationCreatePasswordViewModel
import br.com.tln.personalcard.credenciador.ui.initialization.InitializationLoginViewModel
import br.com.tln.personalcard.credenciador.ui.transactions.DayTransactionsViewModel
import br.com.tln.personalcard.credenciador.ui.transactions.TransactionsViewModel
import br.com.tln.personalcard.credenciador.ui.unlock.UnlockSessionViewModel
import br.com.tln.personalcard.credenciador.ui.welcome.WelcomeViewModel
import br.com.tln.personalcard.credenciador.viewmodel.TelenetViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: TelenetViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    abstract fun bindWelcomeViewModel(welcomeViewModel: WelcomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InitializationLoginViewModel::class)
    abstract fun bindInitializationLoginViewModel(initializationLoginViewModel: InitializationLoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InitializationCreatePasswordViewModel::class)
    abstract fun bindInitializationCreatePasswordViewModel(initializationCreatePasswordViewModel: InitializationCreatePasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangePasswordViewModel::class)
    abstract fun bindChangePasswordViewModel(changePasswordViewModel: ChangePasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UnlockSessionViewModel::class)
    abstract fun bindUnlockSessionViewModel(unlockSessionViewModel: UnlockSessionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BillingValueViewModel::class)
    abstract fun bindBillingValueViewModel(billingValueViewModel: BillingValueViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BillingInstallmentsViewModel::class)
    abstract fun bindBillingInstallmentsViewModel(billingValueViewModel: BillingInstallmentsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BillingQrCodeViewModel::class)
    abstract fun bindBillingQrCodeViewModel(billingValueViewModel: BillingQrCodeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BillingQrCodeReaderViewModel::class)
    abstract fun bindBillingQrCodeReaderViewModel(billingValueViewModel: BillingQrCodeReaderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BillingCardPaymentViewModel::class)
    abstract fun bindBillingCardViewModel(billingCardViewModel: BillingCardPaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BillingPaymentSuccessViewModel::class)
    abstract fun bindBillingPaymentSuccessViewModel(billingPaymentSuccessViewModel: BillingPaymentSuccessViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransactionsViewModel::class)
    abstract fun bindTransactionsViewModel(transactionsViewModel: TransactionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DayTransactionsViewModel::class)
    abstract fun bindDayTransactionsViewModel(dayTransactionsViewModel: DayTransactionsViewModel): ViewModel
}