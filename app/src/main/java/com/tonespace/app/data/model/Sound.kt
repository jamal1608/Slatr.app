package com.tonespace.app.data.model

data class Sound(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: SoundCategory = SoundCategory.CUSTOM,
    val tags: List<String> = emptyList(),
    val audioUrl: String = "",
    val duration: Int = 0,
    val waveformData: List<Float>? = null,
    val coverImageUrl: String? = null,
    val creatorId: String = "",
    val creatorName: String = "",
    val creatorAvatarUrl: String? = null,
    val playCount: Long = 0,
    val likeCount: Int = 0,
    val downloadCount: Int = 0,
    val shareCount: Int = 0,
    val isPremium: Boolean = false,
    val licenseType: LicenseType = LicenseType.ROYALTY_FREE,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val status: SoundStatus = SoundStatus.PUBLISHED,
    val featured: Boolean = false
)

enum class SoundCategory {
    RINGTONES,
    NOTIFICATIONS,
    ALARMS,
    UI_SOUNDS,
    GAME_SOUNDS,
    NATURE,
    MUSIC_LOOPS,
    VOICE,
    FUNNY,
    MEMES,
    WALLPAPERS,
    CUSTOM
}

enum class LicenseType {
    ROYALTY_FREE,
    CREATIVE_COMMONS,
    CUSTOM,
    PREMIUM
}

enum class SoundStatus {
    DRAFT,
    PENDING_REVIEW,
    PUBLISHED,
    REJECTED,
    REMOVED
}

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val bio: String? = null,
    val isCreator: Boolean = false,
    val isPremium: Boolean = false,
    val premiumExpiry: Long? = null,
    val createdAt: Long = 0,
    val soundCount: Int = 0,
    val totalPlays: Long = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val balance: Long = 0,
    val totalEarnings: Long = 0,
    val totalDownloads: Long = 0
)

data class Comment(
    val id: String = "",
    val soundId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val content: String = "",
    val createdAt: Long = 0,
    val likeCount: Int = 0,
    val replies: List<Comment> = emptyList()
)

data class Playlist(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val coverImageUrl: String? = null,
    val soundIds: List<String> = emptyList(),
    val creatorId: String = "",
    val creatorName: String = "",
    val isPublic: Boolean = true,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

data class CategoryStats(
    val category: SoundCategory = SoundCategory.CUSTOM,
    val soundCount: Int = 0,
    val totalPlays: Long = 0
)

data class SearchResult(
    val sounds: List<Sound> = emptyList(),
    val users: List<User> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val hasMore: Boolean = false,
    val nextCursor: String? = null
)

data class ApiResponse<T>(
    val success: Boolean = false,
    val data: T? = null,
    val error: ApiError? = null
)

data class ApiError(
    val code: String = "",
    val message: String = "",
    val details: Map<String, String>? = null
)