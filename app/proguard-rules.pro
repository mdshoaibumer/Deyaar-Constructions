# ============================================================
# DEYAAR CONSTRUCTION - ProGuard/R8 Rules
# ============================================================

# --- Room Database ---
-keep class com.example.data.local.entity.** { *; }
-keep class com.example.data.local.dao.** { *; }
-keep class com.example.data.local.AppDatabase { *; }
-keep class com.example.data.local.Converters { *; }

# --- Domain Enums (used by Room TypeConverters and serialization) ---
-keepclassmembers enum com.example.domain.model.** { *; }

# --- Domain Models (data classes used in Room mapping) ---
-keep class com.example.domain.model.** { *; }

# --- Kotlin Serialization / Moshi (if re-enabled) ---
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# --- Compose ---
# Keep Compose stability metadata
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# --- Vico Charts ---
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# --- Coil Image Loading ---
-keep class coil.** { *; }
-dontwarn coil.**

# --- Timber Logging (strip in release) ---
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# --- Kotlin Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# --- AndroidX Security Crypto ---
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# --- AndroidX Biometric ---
-keep class androidx.biometric.** { *; }

# --- CameraX ---
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# --- DataStore ---
-keep class androidx.datastore.** { *; }

# --- WorkManager ---
-keep class androidx.work.** { *; }
-keep class com.example.core.notifications.ReminderWorker { *; }

# --- Prevent stripping of generic signatures (needed for Flow/StateFlow) ---
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# --- Firebase (if re-enabled) ---
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# --- Google Tink / ErrorProne (used by security-crypto) ---
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.crypto.tink.**

# --- General safety ---
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
