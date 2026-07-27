package com.tonespace.app.data.network

import com.tonespace.app.data.model.ApiResponse
import com.tonespace.app.data.model.CategoryStats
import com.tonespace.app.data.model.Comment
import com.tonespace.app.data.model.Playlist
import com.tonespace.app.data.model.SearchResult
import com.tonespace.app.data.model.Sound
import com.tonespace.app.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface ToneSpaceApi {

    // Auth
    @POST("auth/verify-token")
    suspend fun verifyToken(@Header("Authorization") token: String): Response<ApiResponse<User>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Header("Authorization") refreshToken: String): Response<ApiResponse<AuthTokens>>

    data class AuthTokens(
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Long
    )

    // Sounds
    @GET("sounds")
    suspend fun getSounds(
        @Query("category") category: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("sort") sort: String = "trending",
        @Query("query") query: String? = null
    ): Response<ApiResponse<SoundListResponse>>

    data class SoundListResponse(
        val sounds: List<Sound>,
        val nextCursor: String?,
        val hasMore: Boolean
    )

    @GET("sounds/{id}")
    suspend fun getSound(@Path("id") id: String): Response<ApiResponse<Sound>>

    @GET("sounds/{id}/stream")
    suspend fun getStreamUrl(@Path("id") id: String): Response<ApiResponse<StreamResponse>>

    data class StreamResponse(
        val url: String,
        val expiresAt: Long,
        val quality: String
    )

    @POST("sounds")
    suspend fun uploadSound(
        @Header("Authorization") token: String,
        @Body request: UploadSoundRequest
    ): Response<ApiResponse<Sound>>

    data class UploadSoundRequest(
        val title: String,
        val description: String,
        val category: String,
        val tags: List<String>,
        val audioUrl: String,
        val duration: Int,
        val waveformData: List<Float>?,
        val coverImageUrl: String?,
        val licenseType: String,
        val isPremium: Boolean
    )

    @POST("sounds/{id}/play")
    suspend fun recordPlay(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    @POST("sounds/{id}/like")
    suspend fun toggleLike(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<LikeResponse>>

    data class LikeResponse(
        val liked: Boolean,
        val likeCount: Int
    )

    @POST("sounds/{id}/download")
    suspend fun recordDownload(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    @POST("sounds/{id}/share")
    suspend fun recordShare(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: ShareRequest
    ): Response<ApiResponse<Unit>>

    data class ShareRequest(
        val platform: String
    )

    // Comments
    @GET("sounds/{id}/comments")
    suspend fun getComments(
        @Path("id") soundId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<CommentListResponse>>

    data class CommentListResponse(
        val comments: List<Comment>,
        val nextCursor: String?,
        val hasMore: Boolean
    )

    @POST("sounds/{id}/comments")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Path("id") soundId: String,
        @Body request: AddCommentRequest
    ): Response<ApiResponse<Comment>>

    data class AddCommentRequest(
        val content: String,
        val parentId: String? = null
    )

    // User
    @GET("users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<ApiResponse<User>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<ApiResponse<User>>

    @GET("users/{id}/sounds")
    suspend fun getUserSounds(
        @Path("id") id: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<SoundListResponse>>

    @GET("users/{id}/likes")
    suspend fun getUserLikes(
        @Path("id") id: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<SoundListResponse>>

    @GET("users/{id}/playlists")
    suspend fun getUserPlaylists(
        @Path("id") id: String
    ): Response<ApiResponse<List<Playlist>>>

    @PATCH("users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<User>>

    data class UpdateProfileRequest(
        val displayName: String?,
        val bio: String?,
        val photoUrl: String?
    )

    // Playlists
    @POST("playlists")
    suspend fun createPlaylist(
        @Header("Authorization") token: String,
        @Body request: CreatePlaylistRequest
    ): Response<ApiResponse<Playlist>>

    data class CreatePlaylistRequest(
        val name: String,
        val description: String,
        val coverImageUrl: String?,
        val soundIds: List<String>,
        val isPublic: Boolean
    )

    @PATCH("playlists/{id}")
    suspend fun updatePlaylist(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: UpdatePlaylistRequest
    ): Response<ApiResponse<Playlist>>

    data class UpdatePlaylistRequest(
        val name: String?,
        val description: String?,
        val coverImageUrl: String?,
        val soundIds: List<String>?,
        val isPublic: Boolean?
    )

    @DELETE("playlists/{id}")
    suspend fun deletePlaylist(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    @POST("playlists/{id}/sounds")
    suspend fun addSoundToPlaylist(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: AddSoundRequest
    ): Response<ApiResponse<Playlist>>

    data class AddSoundRequest(
        val soundId: String
    )

    @DELETE("playlists/{id}/sounds/{soundId}")
    suspend fun removeSoundFromPlaylist(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Path("soundId") soundId: String
    ): Response<ApiResponse<Playlist>>

    // Search
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "all",
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<SearchResult>>

    // Categories
    @GET("categories/stats")
    suspend fun getCategoryStats(): Response<ApiResponse<List<CategoryStats>>>

    // Trending / Featured
    @GET("sounds/trending")
    suspend fun getTrending(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<Sound>>>

    @GET("sounds/featured")
    suspend fun getFeatured(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<Sound>>>

    @GET("sounds/new")
    suspend fun getNew(
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Sound>>>

    // Notifications
    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<NotificationListResponse>>

    data class NotificationListResponse(
        val notifications: List<Notification>,
        val nextCursor: String?,
        val hasMore: Boolean,
        val unreadCount: Int
    )

    data class Notification(
        val id: String,
        val type: String,
        val title: String,
        val message: String,
        val data: Map<String, String>?,
        val isRead: Boolean,
        val createdAt: Long
    )

    @POST("notifications/{id}/read")
    suspend fun markNotificationRead(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    @POST("notifications/read-all")
    suspend fun markAllNotificationsRead(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>

    // Premium
    @GET("premium/status")
    suspend fun getPremiumStatus(
        @Header("Authorization") token: String
    ): Response<ApiResponse<PremiumStatus>>

    data class PremiumStatus(
        val isPremium: Boolean,
        val expiryDate: Long?,
        val plan: String?
    )

    @POST("premium/verify-purchase")
    suspend fun verifyPurchase(
        @Header("Authorization") token: String,
        @Body request: VerifyPurchaseRequest
    ): Response<ApiResponse<PremiumStatus>>

    data class VerifyPurchaseRequest(
        val purchaseToken: String,
        val productId: String,
        val platform: String // "google_play" or "apple"
    )

    // Upload URLs
    @POST("upload/audio")
    suspend fun getAudioUploadUrl(
        @Header("Authorization") token: String,
        @Body request: UploadUrlRequest
    ): Response<ApiResponse<UploadUrlResponse>>

    @POST("upload/image")
    suspend fun getImageUploadUrl(
        @Header("Authorization") token: String,
        @Body request: UploadUrlRequest
    ): Response<ApiResponse<UploadUrlResponse>>

    data class UploadUrlRequest(
        val fileName: String,
        val contentType: String,
        val size: Long
    )

    data class UploadUrlResponse(
        val uploadUrl: String,
        val fileUrl: String,
        val expiresAt: Long
    )

    // Reports
    @POST("reports")
    suspend fun reportContent(
        @Header("Authorization") token: String,
        @Body request: ReportRequest
    ): Response<ApiResponse<Unit>>

    data class ReportRequest(
        val type: String, // "sound", "user", "comment", "playlist"
        val targetId: String,
        val reason: String,
        val details: String?
    )
}