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

# move classes to root package

# Preverification is irrelevant for the dex compiler and the Dalvik VM, from
#https://github.com/facebook/proguard/blob/master/examples/android.pro
-dontpreverify

# Reduce the size of the output some more.
-allowaccessmodification

-classobfuscationdictionary windows.txt
-packageobfuscationdictionary windows.txt

-dontwarn java.nio.**
-dontwarn org.jaudiotagger.**
-dontwarn com.google.errorprone.annotations.*

#jaudiotagger
-keep class org.jsoup.** { *; }
-keep class java.nio** { *; }
-keep class org.jaudiotagger.** { *; }

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#If you're targeting any API level less than Android API 27, also include:
#```pro
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# for retrofit

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
# Retain service method parameters.
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# for okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# obfuscate billing
-keep class com.android.vending.billing.**

#rx connectivity
#-dontwarn com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
#-dontwarn io.reactivex.functions.Function
#-dontwarn rx.internal.util.**
#-dontwarn sun.misc.Unsafe