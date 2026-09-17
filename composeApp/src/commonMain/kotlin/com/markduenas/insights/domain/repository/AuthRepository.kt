package com.markduenas.insights.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isSignedIn: Flow<Boolean>
    val isAdmin: Flow<Boolean>
    val currentUserId: String?
    val currentUserEmail: String?

    suspend fun signInWithEmail(email: String, password: String)
    suspend fun registerWithEmail(email: String, password: String)
    suspend fun signInWithGoogle()
    suspend fun signOut()

    /**
     * Permanently deletes the Firebase Auth account.
     * Requires [password] for re-authentication (email/password accounts).
     * Call after remote + local personal data cleanup.
     */
    suspend fun deleteAccount(password: String)
}
