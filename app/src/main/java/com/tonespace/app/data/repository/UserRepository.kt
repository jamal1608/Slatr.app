package com.tonespace.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tonespace.app.data.local.dao.UserDao
import com.tonespace.app.data.local.entity.UserEntity
import com.tonespace.app.data.model.User
import com.tonespace.app.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userDao: UserDao
) {

    private val usersCollection = firestore.collection(Constants.COLLECTION_USERS)

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun signInWithEmail(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("UID is null")
            getUserFromFirestore(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("UID is null")
            val user = User(
                uid = uid,
                email = email,
                displayName = displayName,
                photoUrl = null,
                bio = null,
                isCreator = false,
                isPremium = false,
                premiumExpiry = null,
                createdAt = System.currentTimeMillis(),
                soundCount = 0,
                totalPlays = 0,
                followersCount = 0,
                followingCount = 0
            )
            usersCollection.document(uid).set(user).await()
            cacheUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user?.uid ?: throw IllegalStateException("UID is null")
            val existingDoc = usersCollection.document(uid).get().await()
            if (existingDoc.exists()) {
                getUserFromFirestore(uid)
            } else {
                val user = User(
                    uid = uid,
                    email = result.user?.email ?: "",
                    displayName = result.user?.displayName ?: "User",
                    photoUrl = result.user?.photoUrl?.toString(),
                    bio = null,
                    isCreator = false,
                    isPremium = false,
                    premiumExpiry = null,
                    createdAt = System.currentTimeMillis(),
                    soundCount = 0,
                    totalPlays = 0,
                    followersCount = 0,
                    followingCount = 0
                )
                usersCollection.document(uid).set(user).await()
                cacheUser(user)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(uid: String): Result<User> = withContext(Dispatchers.IO) {
        getUserFromFirestore(uid)
    }

    suspend fun updateProfile(displayName: String?, bio: String?): Result<User> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")
            val updates = mutableMapOf<String, Any>()
            displayName?.let { updates["displayName"] = it }
            bio?.let { updates["bio"] = it }
            usersCollection.document(uid).update(updates).await()
            getUserFromFirestore(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleCreatorMode(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")
            val doc = usersCollection.document(uid).get().await()
            val current = doc.getBoolean("isCreator") ?: false
            usersCollection.document(uid).update("isCreator", !current).await()
            Result.success(!current)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCreatorEarnings(uid: String): Result<Map<String, Long>> = withContext(Dispatchers.IO) {
        try {
            val doc = usersCollection.document(uid).get().await()
            val balance = doc.getLong("balance") ?: 0L
            val totalEarnings = doc.getLong("totalEarnings") ?: 0L
            val totalPlays = doc.getLong("totalPlays") ?: 0L
            val totalDownloads = doc.getLong("totalDownloads") ?: 0L
            Result.success(mapOf(
                "balance" to balance,
                "totalEarnings" to totalEarnings,
                "totalPlays" to totalPlays,
                "totalDownloads" to totalDownloads
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestWithdrawal(amount: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")
            if (amount < Constants.MIN_WITHDRAWAL_CENTS) {
                throw IllegalStateException("Minimum withdrawal is ${Constants.MIN_WITHDRAWAL_CENTS / 100.0}")
            }
            val userDoc = usersCollection.document(uid).get().await()
            val balance = userDoc.getLong("balance") ?: 0L
            if (balance < amount) throw IllegalStateException("Insufficient balance")

            firestore.collection("withdrawals").add(
                mapOf(
                    "userId" to uid,
                    "amount" to amount,
                    "status" to "pending",
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()

            usersCollection.document(uid).update(
                "balance", com.google.firebase.firestore.FieldValue.increment(-amount)
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun getUserFromFirestore(uid: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val doc = usersCollection.document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: throw IllegalStateException("User not found")
            cacheUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun cacheUser(user: User) {
        userDao.insertUser(
            UserEntity(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl,
                bio = user.bio,
                isCreator = user.isCreator,
                isPremium = user.isPremium,
                createdAt = user.createdAt
            )
        )
    }
}