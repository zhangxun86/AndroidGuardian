plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.zzsr.guardian"
    compileSdk = 34

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
    //implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
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

    // --- AMPS SDKs ---
    implementation("io.github.zhangxun86:amps-asnp-adapter:5.1.5213.0")
    implementation("io.github.zhangxun86:amps-bd-adapter:5.1.940.0")
    implementation("io.github.zhangxun86:amps-bz-adapter:5.1.52121.0")
    implementation("io.github.zhangxun86:amps-csj-adapter:5.1.7012.0")
    implementation("io.github.zhangxun86:amps-gdt-adapter:5.1.46421512.0")
    implementation("io.github.zhangxun86:amps-gm-adapter:5.1.7012.0")
    implementation("io.github.zhangxun86:amps-hw-adapter:5.1.13478301.0")
    implementation("io.github.zhangxun86:amps-jd-adapter:5.1.2632.0")
    implementation("io.github.zhangxun86:amps-ks-adapter:5.1.46301.1")
    implementation("io.github.zhangxun86:amps-ms-adapter:5.1.2566.4")
    implementation("io.github.zhangxun86:amps-oct-adapter:5.1.1641.1")
    implementation("io.github.zhangxun86:amps-qm-adapter:5.1.347310439.0")
    implementation("io.github.zhangxun86:amps-sdk:5.1.0.34")
    implementation("io.github.zhangxun86:amps-sigmob-adapter:5.1.4240.0")
    implementation("io.github.zhangxun86:asnp-sdk:5.2.1.3")

// --- Third-party SDKs ---
    implementation("io.github.zhangxun86:baidu-mobads-sdk:9.40")
    implementation("io.github.zhangxun86:beizi-fusion-sdk:5.2.1.21")
    implementation("io.github.zhangxun86:common:5.1.0.31")
    implementation("io.github.zhangxun86:gdtsdk-union-normal:4.642.1512")
    implementation("io.github.zhangxun86:kssdk-ad:4.6.30.1")
    implementation("io.github.zhangxun86:ms-sdk:2.5.6.6_release")
    implementation("io.github.zhangxun86:octopus-ad-sdk:1.6.4.1")
    implementation("io.github.zhangxun86:open-ad-sdk:7.0.1.2")
    implementation("io.github.zhangxun86:qumeng:3.473.10.439")
    implementation("io.github.zhangxun86:wind-common:1.8.3")
    implementation("io.github.zhangxun86:wind-sdk:4.24.0")
}