

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.west2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.west2"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    // 定义签名配置（测试）
//    signingConfigs {
//        // 创建名为 "release" 的签名配置（复用 debug 密钥，仅用于测试）
//        create("release") {
//            keyAlias = "androiddebugkey"       // 默认 debug 密钥别名
//            keyPassword = "android"            // 默认 debug 密钥密码
//            storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore") // debug.keystore 绝对路径
//            storePassword = "android"          // 默认 debug 密钥库密码
//        }
//    }

    //minify
    buildTypes {
        release {
            // 核心开关：开启 Minify（R8）
            isMinifyEnabled = true

            // 开启资源压缩（配合 Minify，移除无用资源，比如未引用的图片、字符串）
            isShrinkResources = true

            // 指定混淆规则文件（默认包含 Android 官方规则 + 自定义规则）
            proguardFiles(
                // Android 官方默认规则（必须保留，否则会破坏系统 API 调用）
                getDefaultProguardFile("proguard-android-optimize.txt"),
                // 自定义混淆规则文件（项目根目录的 proguard-rules.pro）
                "proguard-rules.pro"
            )
            //release临时签名
//            signingConfig = signingConfigs.getByName("release")
        }

        // debug 包建议关闭，避免调试时类名混淆、断点失效
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.compose.navigation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navigation 核心库
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
// Lifecycle 库，确保 LifecycleOwner 可用
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Coil 图片加载
    implementation ("io.coil-kt:coil-compose:2.5.0")

}

dependencies {

    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:$room_version")

// If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
// See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

// If this project only uses Java source, use the Java annotationProcessor
// No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

// optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

// optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

// optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")

// optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")

// optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

// optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")
}


dependencies{
    implementation(libs.androidx.compose.runtime)// 1. Retrofit 核心 + Gson 解析
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// 2. 协程（Compose 异步核心）
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// 3. ViewModel + Compose 集成
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// 4. 可选：OkHttp 日志拦截器（调试用）
    debugImplementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")
}