-optimizationpasses 5
-dontusemixedcaseclassnames
-repackageclasses ''
-allowaccessmodification
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

#-keep class com.dwdm.postcard.presentation.creation.model.**
#-keep interface com.dwdm.postcard.presentation.creation.model.**

-keep public class android.net.http.**
-keep public class com.google.gson.**
-keep public class com.google.gson.** {public private protected *;}
-keepclasseswithmembers class * {
    native <methods>;
}

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}

-keep public class * extends android.content.Context

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.* { *; }
-keep interface okhttp3.* { *; }
-keep class com.facebook.* { *; }
-keep interface com.facebook.* { *; }
#Picasso
-dontwarn com.squareup.okhttp.**

# Retrofit #
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-dontwarn com.squareup.picasso.*
-dontwarn java.nio.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn org.codehaus.mojo.**
-dontwarn okio.**
-dontwarn rx.internal.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}


# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep,allowobfuscation @interface com.facebook.soloader.DoNotOptimize

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Do not strip any method/class that is annotated with @DoNotOptimize
-keep @com.facebook.soloader.DoNotOptimize class *
-keepclassmembers class * {
    @com.facebook.soloader.DoNotOptimize *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox-.**
-dontwarn com.facebook.infer.**

-keepattributes Annotation
-keepattributes Signature
-keep class ru.yandex.** { public *; }
-keep class com.yandex.metrica.** {public *;}
-keep interface ru.yandex.** { *; }
-dontwarn ru.yandex.**


-keep class com.threatmetrix.TrustDefender.** { *; }
# Required to suppress warning messages about ThreatMetrix SDK
-dontwarn com.threatmetrix.TrustDefender.**

# ThreatMetris SDK wants OkHttp to be available like this :(
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn javax.annotation.Nullable
-dontwarn org.conscrypt.OpenSSLProvider
-dontwarn org.conscrypt.Conscrypt
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keepattributes InnerClasses
 -keep class **.R
 -keep class **.R$* {
    <fields>;
}

-keep class com.samsung.** { *; }
-keep interface com.samsung.** { *; }
-dontwarn com.samsung.**

-keep class com.google.android.gms.** { *; }
-keep class com.example.firebasechat.** { *; }
