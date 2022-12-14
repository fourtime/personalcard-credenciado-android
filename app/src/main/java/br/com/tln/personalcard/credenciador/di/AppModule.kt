/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import br.com.tln.personalcard.credenciador.APP_CONFIG_SHARED_PREFERENCES
import br.com.tln.personalcard.credenciador.AUTHENTICATION_OK_HTTP_CLIENT
import br.com.tln.personalcard.credenciador.AUTHENTICATION_TELENET_SERVICE
import br.com.tln.personalcard.credenciador.BuildConfig
import br.com.tln.personalcard.credenciador.TRANSACTION_OK_HTTP_CLIENT
import br.com.tln.personalcard.credenciador.SESSION_SHARED_PREFERENCES
import br.com.tln.personalcard.credenciador.moshi.BigDecimalJsonAdapter
import br.com.tln.personalcard.credenciador.moshi.TelenetBaseResponseJsonAdapter
import br.com.tln.personalcard.credenciador.okhttp.authenticator.TelenetAuthenticator
import br.com.tln.personalcard.credenciador.okhttp.interceptor.TimeoutInterceptor
import br.com.tln.personalcard.credenciador.provider.ResourceProvider
import br.com.tln.personalcard.credenciador.provider.ResourceProviderImpl
import br.com.tln.personalcard.credenciador.webservice.TransactionService
import br.com.tln.personalcard.credenciador.webservice.TelenetService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Suppress("unused")
@Module(includes = [ViewModelModule::class, DbModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideResourceProvider(application: Application): ResourceProvider {
        return ResourceProviderImpl(application = application)
    }

    @Singleton
    @Provides
    @Named(SESSION_SHARED_PREFERENCES)
    fun provideSessionSharedPreference(application: Application): SharedPreferences {
        return application.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    @Named(APP_CONFIG_SHARED_PREFERENCES)
    fun provideAppConfigSharedPreference(application: Application): SharedPreferences {
        return application.getSharedPreferences(APP_CONFIG_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        timeoutInterceptor: TimeoutInterceptor,
        telenetAuthenticator: TelenetAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(timeoutInterceptor)
            .addInterceptor(HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY })
            .authenticator(telenetAuthenticator)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @Named(AUTHENTICATION_OK_HTTP_CLIENT)
    fun provideAuthenticatorOkHttpClient(timeoutInterceptor: TimeoutInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(timeoutInterceptor)
            .addInterceptor(HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @Named(TRANSACTION_OK_HTTP_CLIENT)
    fun transactionOkHttpClient(
        telenetAuthenticator: TelenetAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            })
            .authenticator(telenetAuthenticator)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(BigDecimalJsonAdapter())
            .add(TelenetBaseResponseJsonAdapter.Factory())
            .build().also {
                println(it)
            }
    }

    @Singleton
    @Provides
    fun provideRetrofitConverterFactory(moshi: Moshi): Converter.Factory {
        return MoshiConverterFactory.create(moshi)
    }

    @Singleton
    @Provides
    @Named(AUTHENTICATION_TELENET_SERVICE)
    fun provideAuthenticatorTelenetService(
        @Named(AUTHENTICATION_OK_HTTP_CLIENT) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): TelenetService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(TelenetService::class.java)
    }

    @Singleton
    @Provides
    fun transactionService(
        @Named(TRANSACTION_OK_HTTP_CLIENT) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): TransactionService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(TransactionService::class.java)
    }

    @Singleton
    @Provides
    fun provideTelenetService(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): TelenetService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(TelenetService::class.java)
    }
}