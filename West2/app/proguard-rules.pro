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

## Add project specific ProGuard rules here.

 ## 基础配置：保留行号信息（便于调试崩溃日志）
 -keepattributes SourceFile,LineNumberTable
 -renamesourcefileattribute SourceFile
 #
 ## 1. 基础保留规则
 ## 保留四大组件（Android 官方默认规则已包含，此处仅显式声明增强可读性）
 -keep public class * extends android.app.Activity
 -keep public class * extends androidx.fragment.app.Fragment
 -keep public class * extends android.app.Service
 -keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.content.ContentProvider
 #
 # 2. 序列化/反序列化核心保留（Gson/Retrofit）
 # 保留实体类（替换为你项目的实际包名）
-keep class com.example.west2.data.model** { *; }
-keepclassmembers class com.example.west2.data.model.** {
    <fields>;      # 保留所有字段
    <init>(...);   # 保留所有构造方法（尤其是无参构造）
}


 ## 保留泛型和注解（Gson/Retrofit 必需）
-keepattributes Signature, *Annotation*

 ## 保留 Gson 核心类
-keepattributes Signature, *Annotation*  # 保留泛型和注解
-keep,allowobfuscation class com.google.gson.reflect.TypeToken
-keep,allowobfuscation class * extends com.google.gson.reflect.TypeToken
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer


 # ========== 只禁用你的实体类的优化 =========
 # ========== 允许其他类正常优化 ==========
 # 保留 R8 对非 Gson/实体类的优化（如压缩、混淆）
 -optimizations !code/simplification/cast,!field/*,!class/merging/*
 -optimizationpasses 5
 -dontpreverify

 ## 3. 第三方库保留规则

 -keep class androidx.compose.** { *; }
 -keep class com.example.west2.ui.** { *; }

 # OkHttp（网络请求）
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.google.gson.**

 # 谷歌定位 SDK
 -keep class com.google.android.gms.location.** { *; }
 -keep class com.google.android.gms.common.api.** { *; }
 #
 ## 4. 其他必要保留
 ## 枚举类（避免枚举值被混淆）
 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }
 ## JNI 本地方法
 -keepclasseswithmembernames class * {
     native <methods>;
 }
 ## 定位相关系统类
 -keep class android.location.Geocoder { *; }
 -keep class android.location.Address { *; }


 ## 5. 警告忽略（仅消除无关警告，不影响功能）
 -dontwarn sun.misc.**
 -dontwarn okhttp3.**
 -dontwarn com.google.gson.**


# 6.Retrofit 混淆以后发生 ParameterizedType error 解决办法
  -keep,allowobfuscation,allowshrinking interface retrofit2.Call
  -keep,allowobfuscation,allowshrinking class retrofit2.Response
  # With R8 full mode generic signatures are stripped for classes that are not、

  # kept. Suspend functions are wrapped in continuations where the type argument
  # is used.
  -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation