# ProGuard rules for ToneShare

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }
-keep class * extends dagger.hilt.android.HiltApplication { *; }
-keep class * extends dagger.hilt.android.HiltActivity { *; }

# Keep Room entities and DAOs
-keep class com.tonespace.app.data.local.** { *; }

# Keep serialization classes
-keep class kotlinx.serialization.** { *; }
-keep class com.tonespace.app.data.model.** { *; }

# Keep ExoPlayer classes
-keep class com.google.android.exoplayer2.** { *; }

# Keep Coil classes
-keep class coil.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }

# Keep AdMob classes
-keep class com.google.android.gms.ads.** { *; }

# Keep Hilt ViewModel factories
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
}