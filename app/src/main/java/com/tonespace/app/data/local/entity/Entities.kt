package com.tonespace.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_sounds")
data class SoundEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val tags: String,
    val audioUrl: String,
    val localPath: String?,
    val duration: Int,
    val coverImageUrl: String?,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatarUrl: String?,
    val playCount: Long,
    val likeCount: Int,
    val downloadCount: Int,
    val isPremium: Boolean,
    val isLiked: Boolean,
    val isDownloaded: Boolean,
    val createdAt: Long,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val bio: String?,
    val isCreator: Boolean,
    val isPremium: Boolean,
    val createdAt: Long
)