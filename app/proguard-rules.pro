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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-dontwarn
#-ignorewarnings

-dontwarn java.nio.**
-dontwarn org.jaudiotagger.**
-dontwarn com.google.errorprone.annotations.*

-keep class org.jsoup.** { *; }
-keep class java.nio** { *; }
-keep class org.jaudiotagger.** { *; }

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class org.tensorflow.** { *; }

-keep class com.android.vending.billing.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}