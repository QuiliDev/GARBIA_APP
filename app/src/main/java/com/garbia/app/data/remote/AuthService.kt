package com.garbia.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor() {

    // Mismo patrón try/catch que FirestoreService — null si no hay google-services.json
    private val auth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        null
    }

    val currentUser: FirebaseUser? get() = auth?.currentUser
    fun currentUid(): String? = auth?.currentUser?.uid
    fun isSignedIn(): Boolean = auth?.currentUser != null

    suspend fun signInAnonymously(): String? {
        val firebaseAuth = auth ?: return null
        return try {
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.currentUser?.uid
            } else {
                firebaseAuth.signInAnonymously().await().user?.uid
            }
        } catch (e: Exception) {
            null
        }
    }

    fun signOut() {
        auth?.signOut()
    }
}
