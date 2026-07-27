package com.tonespace.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tonespace.app.data.local.entity.SoundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundDao {
    @Query("SELECT * FROM cached_sounds ORDER BY createdAt DESC")
    fun getAllSounds(): Flow<List<SoundEntity>>

    @Query("SELECT * FROM cached_sounds WHERE category = :category ORDER BY createdAt DESC")
    fun getSoundsByCategory(category: String): Flow<List<SoundEntity>>

    @Query("SELECT * FROM cached_sounds WHERE id = :id")
    suspend fun getSoundById(id: String): SoundEntity?

    @Query("SELECT * FROM cached_sounds WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchSounds(query: String): Flow<List<SoundEntity>>

    @Query("SELECT * FROM cached_sounds WHERE isLiked = 1")
    fun getLikedSounds(): Flow<List<SoundEntity>>

    @Query("SELECT * FROM cached_sounds WHERE isDownloaded = 1")
    fun getDownloadedSounds(): Flow<List<SoundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSounds(sounds: List<SoundEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSound(sound: SoundEntity)

    @Update
    suspend fun updateSound(sound: SoundEntity)

    @Delete
    suspend fun deleteSound(sound: SoundEntity)

    @Query("DELETE FROM cached_sounds WHERE cachedAt < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)

    @Query("UPDATE cached_sounds SET isLiked = :liked, likeCount = likeCount + :delta WHERE id = :id")
    suspend fun updateLikeStatus(id: String, liked: Boolean, delta: Int)

    @Query("UPDATE cached_sounds SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: String)

    @Query("UPDATE cached_sounds SET downloadCount = downloadCount + 1, isDownloaded = 1 WHERE id = :id")
    suspend fun incrementDownloadCount(id: String)
}