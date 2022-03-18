package com.milkyhead.android.payconiq.di

import com.milkyhead.android.payconiq.data.remote.DefaultGithubRemoteDataSource
import com.milkyhead.android.payconiq.data.remote.GithubRemoteDataSource
import com.milkyhead.android.payconiq.data.repository.DefaultGithubUserRepository
import com.milkyhead.android.payconiq.domain.repository.GithubUserRepository
import com.milkyhead.android.payconiq.domain.usecase.DefaultGetUserDetailsUseCase
import com.milkyhead.android.payconiq.domain.usecase.DefaultSearchUserUseCase
import com.milkyhead.android.payconiq.domain.usecase.GetUserDetailsUseCase
import com.milkyhead.android.payconiq.domain.usecase.SearchUserUseCase
import com.milkyhead.android.payconiq.presentation.error.DefaultErrorHandler
import com.milkyhead.android.payconiq.presentation.error.ErrorHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Suppress("UNUSED")
@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationBindingModule {

    @Binds
    @Singleton
    internal abstract fun bindGithubRemoteDataSource(
        defaultGithubRemoteDataSource: DefaultGithubRemoteDataSource
    ): GithubRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindGithubUserRepository(
        defaultGithubUserRepository: DefaultGithubUserRepository
    ): GithubUserRepository

    @Binds
    internal abstract fun bindGetUserDetailsUseCase(
        defaultGetUserDetailsUseCase: DefaultGetUserDetailsUseCase
    ): GetUserDetailsUseCase

    @Binds
    internal abstract fun bindSearchUserUseCase(
        defaultSearchUserUseCase: DefaultSearchUserUseCase
    ): SearchUserUseCase

    @Binds
    @Singleton
    internal abstract fun bindErrorHandler(
        defaultErrorHandler: DefaultErrorHandler
    ): ErrorHandler
}