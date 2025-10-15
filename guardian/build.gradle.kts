plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.zzsr.guardian"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    api(libs.androidx.lifecycle.process)
    // +++ 新增 OkHttp3 依赖 +++
    // 使用 implementation，因为它是库的内部实现细节
    implementation(libs.okhttp)
    // 日志拦截器在调试时非常有用，作为库的一部分是很好的实践
    implementation(libs.okhttp.logging.interceptor)

    //京东渠道SDK
    implementation("com.github.JAD-FE-TEAM.JADYunAndroid:jad_yun_sdk:2.6.28")

    //华为渠道SDK
    implementation("com.huawei.hms:ads-lite:13.4.78.301")
    implementation("com.huawei.hms:ads-consent:3.4.78.301")
}