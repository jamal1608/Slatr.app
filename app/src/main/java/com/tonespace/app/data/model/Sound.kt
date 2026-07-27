package com.tonespace.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Sound(
    val id: String,
    val title: String,
    val description: String,
    val category: SoundCategory,
    val tags: List<String>,
    val audioUrl: String,
    val duration: Int, // seconds
    val waveformData: List<Float>?, // for visualization
    val coverImageUrl: String?,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatarUrl: String?,
    val playCount: Long,
    val likeCount: Int,
    val downloadCount: Int,
    val shareCount: Int,
    val isPremium: Boolean,
    val licenseType: LicenseType,
    val createdAt: Long,
    val updatedAt: Long,
    val status: SoundStatus = SoundStatus.PUBLISHED
)

enum class SoundCategory(@Serializable serialName: String) {
    @SerialName("ringtones") RINGTONES,
    @SerialName("notifications") NOTIFICATIONS,
    @SerialName("alarms") ALARMS,
    @SerialName("ui_sounds") UI_SOUNDS,
    @SerialName("game_sounds") GAME_SOUNDS,
    @SerialName("nature") NATURE,
    @SerialName("music_loops") MUSIC_LOOPS,
    @SerialName("voice") VOICE,
    @SerialName("funny") FUNNY,
    @SerialName("memes") MEMES,
    @SerialName("custom") CUSTOM
}

enum class LicenseType(@Serializable serialName: String) {
    @SerialName("royalty_free") ROYALTY_FREE,
    @SerialName("creative_commons") CREATIVE_COMMONS,
    @SerialName("custom") CUSTOM,
    @SerialName("premium") PREMIUM
}

enum class SoundStatus(@Serializable serialName: String) {
    @SerialName("draft") DRAFT,
    @SerialName("pending_review") PENDING_REVIEW,
    @SerialName("published") PUBLISHED,
    @SerialName("rejected") REJECTED,
    @SerialName("removed") REMOVED
}

@Serializable
data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val bio: String?,
    val isCreator: Boolean,
    val isPremium: Boolean,
    val premiumExpiry: Long?,
    val createdAt: Long,
    val soundCount: Int,
    val totalPlays: Long,
    val followersCount: Int,
    val followingCount: Int
)

@Serializable
data class Comment(
    val id: String,
    val soundId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String?,
    val content: String,
    val createdAt: Long,
    val likeCount: Int,
    val replies: List<Comment> = emptyList()
)

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val coverImageUrl: String?,
    val soundIds: List<String>,
    val creatorId: String,
    val creatorName: String,
    val isPublic: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class CategoryStats(
    val category: SoundCategory,
    val soundCount: Int,
    val totalPlays: Long
)

@Serializable
data class SearchResult(
    val sounds: List<Sound>,
    val users: List<User>,
    val playlists: List<Playlist>,
    val hasMore: Boolean,
    val nextCursor: String?
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ApiError?
)

@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)