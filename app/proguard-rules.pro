# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep EditorKit classes
-keep class com.blacksquircle.ui.** { *; }
-keep class com.blacksquircle.ui.language.** { *; }

# Keep JGit classes
-keep class org.eclipse.jgit.** { *; }

# Kotlin
-keepattributes *Annotation*
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
