# Kassenbuch ProGuard Rules

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Koin
-keep class de.kassenbuch.di.** { *; }

# Timber
-dontwarn org.jetbrains.annotations.**

# Gson
-keepattributes Signature
-keep class de.kassenbuch.data.entity.** { *; }

# Apache POI
-dontwarn org.apache.poi.**
-keep class org.apache.poi.** { *; }
