# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-renamesourcefileattribute SourceFile

# Preserve Jetpack Compose annotations
-keepattributes *Annotation*
-keepclassmembers class ** {
    @androidx.compose.runtime.Immutable *;
    @androidx.compose.runtime.Stable *;
}

# Keep Firebase models (avoid minification breaking mapping)
-keepclassmembers class id.ac.umkt.kel_10_mk.projectuas.models.** { *; }
-keep class id.ac.umkt.kel_10_mk.projectuas.models.** { *; }

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable