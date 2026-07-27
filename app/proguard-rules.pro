-keep class com.tonespace.app.** { *; }
-keep class dagger.hilt.** { *; }
-keep class * implements dagger.hilt.android.internal.managers.BindingModule { *; }
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}