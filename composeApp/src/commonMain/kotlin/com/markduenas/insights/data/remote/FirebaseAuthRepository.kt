package com.markduenas.insights.data.remote

import com.markduenas.insights.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseAuthRepository(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val isSignedIn: Flow<Boolean> =
        auth.authStateChanged.map { it != null }

    override val isAdmin: Flow<Boolean> =
        auth.authStateChanged.map { user ->
            if (user == null) return@map false
            try {
                val result = user.getIdTokenResult(false)
                result.claims["admin"] as? Boolean ?: false
            } catch (_: Exception) {
                false
            }
        }

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun registerWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithGoogle() {
        // Google Sign-In requires platform-specific credential handling.
        // Implement via expect/actual in Phase 3 expansion.
        throw NotImplementedError("Google Sign-In not yet implemented")
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
