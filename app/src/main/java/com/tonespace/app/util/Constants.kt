package com.tonespace.app.util

object Constants {
    const val DATABASE_NAME = "tonespace_database"
    const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L

    const val COLLECTION_SOUNDS = "sounds"
    const val COLLECTION_USERS = "users"
    const val COLLECTION_COMMENTS = "comments"
    const val COLLECTION_PLAYLISTS = "playlists"
    const val COLLECTION_TRANSACTIONS = "transactions"
    const val COLLECTION_NOTIFICATIONS = "notifications"

    const val STORAGE_SOUNDS = "sounds"
    const val STORAGE_COVERS = "covers"
    const val STORAGE_AVATARS = "avatars"

    const val TOKEN_VALUE_CENTS = 1
    const val MIN_WITHDRAWAL_CENTS = 1000
    const val CREATOR_REVENUE_SHARE = 0.70
    const val APP_REVENUE_SHARE = 0.30

    const val EARN_RATE_PER_PLAY = 0.001
    const val EARN_RATE_PER_DOWNLOAD = 0.01
    const val EARN_RATE_PER_REWARDED_AD = 0.05

    const val ADMOB_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val ADMOB_INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val ADMOB_REWARDED_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    const val PREMIUM_PRODUCT_MONTHLY = "premium_monthly"
    const val PREMIUM_PRODUCT_YEARLY = "premium_yearly"
    const val PREMIUM_PRODUCT_LIFETIME = "premium_lifetime"

    const val MIN_UPLOAD_DURATION = 5
    const val MAX_UPLOAD_DURATION = 120
    const val MAX_FILE_SIZE_MB = 10
    const val ALLOWED_AUDIO_TYPES = "audio/mpeg,audio/mp4,audio/ogg,audio/wav,audio/aac"
}