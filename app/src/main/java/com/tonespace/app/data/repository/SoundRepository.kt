package com.tonespace.app.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.tonespace.app.data.local.dao.SoundDao
import com.tonespace.app.data.local.entity.SoundEntity
import com.tonespace.app.data.model.Sound
import com.tonespace.app.data.model.SoundCategory
import com.tonespace.app.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val soundDao: SoundDao
) {

    private val soundsCollection = firestore.collection(Constants.COLLECTION_SOUNDS)

    fun getTrendingSounds(): Flow<List<SoundEntity>> = soundDao.getAllSounds()

    fun getSoundsByCategory(category: SoundCategory): Flow<List<SoundEntity>> {
        return soundDao.getSoundsByCategory(category.name)
    }

    fun searchSounds(query: String): Flow<List<SoundEntity>> {
        return soundDao.searchSounds(query)
    }

    fun getLikedSounds(): Flow<List<SoundEntity>> = soundDao.getLikedSounds()

    fun getDownloadedSounds(): Flow<List<SoundEntity>> = soundDao.getDownloadedSounds()

    suspend fun fetchTrendingSounds(limit: Long = 20): Result<List<Sound>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = soundsCollection
                .orderBy("playCount", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            val sounds = snapshot.toObjects(Sound::class.java)
            cacheSounds(sounds)
            Result.success(sounds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchNewSounds(limit: Long = 20): Result<List<Sound>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = soundsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            val sounds = snapshot.toObjects(Sound::class.java)
            cacheSounds(sounds)
            Result.success(sounds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchFeaturedSounds(limit: Long = 10): Result<List<Sound>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = soundsCollection
                .whereEqualTo("featured", true)
                .orderBy("playCount", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            val sounds = snapshot.toObjects(Sound::class.java)
            cacheSounds(sounds)
            Result.success(sounds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSoundById(id: String): Result<Sound?> = withContext(Dispatchers.IO) {
        try {
            val cached = soundDao.getSoundById(id)
            if (cached != null) {
                val sound = Sound(
                    id = cached.id, title = cached.title, description = cached.description,
                    category = SoundCategory.valueOf(cached.category),
                    tags = cached.tags.split(",").filter { it.isNotBlank() },
                    audioUrl = cached.audioUrl, duration = cached.duration,
                    coverImageUrl = cached.coverImageUrl, creatorId = cached.creatorId,
                    creatorName = cached.creatorName, creatorAvatarUrl = cached.creatorAvatarUrl,
                    playCount = cached.playCount, likeCount = cached.likeCount,
                    downloadCount = cached.downloadCount, isPremium = cached.isPremium,
                    isLiked = cached.isLiked, createdAt = cached.createdAt
                )
                return@withContext Result.success(sound)
            }

            val doc = soundsCollection.document(id).get().await()
            val sound = doc.toObject(Sound::class.java)
            sound?.let { cacheSounds(listOf(it)) }
            Result.success(sound)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadSound(
        title: String,
        description: String,
        category: SoundCategory,
        tags: List<String>,
        audioBytes: ByteArray,
        duration: Int,
        coverImageBytes: ByteArray?,
        isPremium: Boolean
    ): Result<Sound> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")
            val soundId = UUID.randomUUID().toString()

            val audioRef = storage.reference.child("${Constants.STORAGE_SOUNDS}/$soundId.mp3")
            audioRef.putBytes(audioBytes).await()
            val audioUrl = audioRef.downloadUrl.await().toString()

            var coverUrl: String? = null
            coverImageBytes?.let { bytes ->
                val coverRef = storage.reference.child("${Constants.STORAGE_COVERS}/$soundId.jpg")
                coverRef.putBytes(bytes).await()
                coverUrl = coverRef.downloadUrl.await().toString()
            }

            val userDoc = firestore.collection(Constants.COLLECTION_USERS).document(userId).get().await()
            val creatorName = userDoc.getString("displayName") ?: "Unknown"
            val creatorAvatar = userDoc.getString("photoUrl")

            val sound = Sound(
                id = soundId,
                title = title,
                description = description,
                category = category,
                tags = tags,
                audioUrl = audioUrl,
                duration = duration,
                waveformData = null,
                coverImageUrl = coverUrl,
                creatorId = userId,
                creatorName = creatorName,
                creatorAvatarUrl = creatorAvatar,
                playCount = 0,
                likeCount = 0,
                downloadCount = 0,
                shareCount = 0,
                isPremium = isPremium,
                licenseType = com.tonespace.app.data.model.LicenseType.ROYALTY_FREE,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            soundsCollection.document(soundId).set(sound).await()
            cacheSounds(listOf(sound))
            Result.success(sound)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLike(soundId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")
            val likeRef = firestore.collection("likes").document("${userId}_$soundId")
            val doc = likeRef.get().await()

            if (doc.exists()) {
                likeRef.delete().await()
                soundsCollection.document(soundId).update("likeCount", com.google.firebase.firestore.FieldValue.increment(-1)).await()
                soundDao.updateLikeStatus(soundId, false, -1)
                Result.success(false)
            } else {
                likeRef.set(mapOf("userId" to userId, "soundId" to soundId, "createdAt" to System.currentTimeMillis())).await()
                soundsCollection.document(soundId).update("likeCount", com.google.firebase.firestore.FieldValue.increment(1)).await()
                soundDao.updateLikeStatus(soundId, true, 1)
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordPlay(soundId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            soundsCollection.document(soundId)
                .update("playCount", com.google.firebase.firestore.FieldValue.increment(1)).await()
            soundDao.incrementPlayCount(soundId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordDownload(soundId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            soundsCollection.document(soundId)
                .update("downloadCount", com.google.firebase.firestore.FieldValue.increment(1)).await()
            soundDao.incrementDownloadCount(soundId)

            val userId = auth.currentUser?.uid
            if (userId != null) {
                val earnings = (Constants.EARN_RATE_PER_DOWNLOAD * 100).toLong()
                firestore.collection("transactions").add(
                    mapOf(
                        "userId" to userId,
                        "soundId" to soundId,
                        "type" to "download_earning",
                        "amount" to earnings,
                        "createdAt" to System.currentTimeMillis()
                    )
                ).await()
                firestore.collection(Constants.COLLECTION_USERS).document(userId)
                    .update("balance", com.google.firebase.firestore.FieldValue.increment(earnings)).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSound(soundId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            soundsCollection.document(soundId).delete().await()
            storage.reference.child("${Constants.STORAGE_SOUNDS}/$soundId.mp3").delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun cacheSounds(sounds: List<Sound>) {
        val entities = sounds.map { sound ->
            SoundEntity(
                id = sound.id,
                title = sound.title,
                description = sound.description,
                category = sound.category.name,
                tags = sound.tags.joinToString(","),
                audioUrl = sound.audioUrl,
                localPath = null,
                duration = sound.duration,
                coverImageUrl = sound.coverImageUrl,
                creatorId = sound.creatorId,
                creatorName = sound.creatorName,
                creatorAvatarUrl = sound.creatorAvatarUrl,
                playCount = sound.playCount,
                likeCount = sound.likeCount,
                downloadCount = sound.downloadCount,
                isPremium = sound.isPremium,
                isLiked = false,
                isDownloaded = false,
                createdAt = sound.createdAt
            )
        }
        soundDao.insertSounds(entities)
    }
}