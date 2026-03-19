package com.markduenas.insights.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isSignedIn: Flow<Boolean>
    val isAdmin: Flow<Boolean>
    val currentUserId: String?

    suspend fun signInWithEmail(email: String, password: String)
    suspend fun registerWithEmail(email: String, password: String)
    suspend fun signInWithGoogle()
    suspend fun signOut()
}
