package com.markduenas.insights.di

import com.markduenas.insights.data.local.PersonalInsightRepositoryImpl
import com.markduenas.insights.data.local.db.InsightsDatabase
import com.markduenas.insights.data.remote.FirebaseAuthRepository
import com.markduenas.insights.data.remote.FirestoreInsightRepository
import com.markduenas.insights.domain.repository.AuthRepository
import com.markduenas.insights.domain.repository.InsightRepository
import com.markduenas.insights.domain.repository.PersonalInsightRepository
import com.markduenas.insights.domain.usecase.FindMatchingInsightUseCase
import com.markduenas.insights.presentation.feedback.FeedbackScreenModel
import com.markduenas.insights.presentation.admin.AdminScreenModel
import com.markduenas.insights.presentation.auth.SignUpScreenModel
import com.markduenas.insights.presentation.auth.SignInScreenModel
import com.markduenas.insights.presentation.detail.InsightDetailScreenModel
import com.markduenas.insights.presentation.home.HomeScreenModel
import com.markduenas.insights.presentation.personal.AddInsightScreenModel
import com.markduenas.insights.presentation.personal.PersonalInsightsScreenModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val appModule = module {
    // Firebase
    single { Firebase.firestore }
    single { Firebase.auth }

    // Repositories
    single<InsightRepository> { FirestoreInsightRepository(get()) }
    single<AuthRepository> { FirebaseAuthRepository(get()) }
    single { InsightsDatabase(get()) }
    single<PersonalInsightRepository> { PersonalInsightRepositoryImpl(get(), get()) }

    // Use cases
    factory { FindMatchingInsightUseCase(get()) }

    // Screen models
    factory { HomeScreenModel(get(), get()) }
    factory { (insightId: String) -> InsightDetailScreenModel(get(), insightId) }
    factory { SignInScreenModel(get()) }
    factory { SignUpScreenModel(get()) }
    factory { PersonalInsightsScreenModel(get()) }
    factory { AddInsightScreenModel(get(), get(), get()) }
    factory { AdminScreenModel(get(), get()) }
    factory { FeedbackScreenModel(get(), get()) }
}

val domainModule = module {}

/** Common modules — combine with the platform module when calling startKoin. */
val commonModules = listOf(appModule, domainModule)
