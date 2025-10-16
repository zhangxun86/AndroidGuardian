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

    api("xyz.adscope.amps.AMPSSDK:AMPSASNPAdapter:5.1.5213.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSBDAdapter:5.1.940.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSBZAdapter:5.1.52121.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSCSJAdapter:5.1.7012.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSGDTAdapter:5.1.46421512.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSGMAdapter:5.1.7012.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSHWAdapter:5.1.13478301.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSJDAdapter:5.1.2632.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSKSAdapter:5.1.46301.1")
    api("xyz.adscope.amps.AMPSSDK:AMPSMSAdapter:5.1.2566.4")
    api("xyz.adscope.amps.AMPSSDK:AMPSOCTAdapter:5.1.1641.1")
    api("xyz.adscope.amps.AMPSSDK:AMPSQMAdapter:5.1.347310439.0")
    api("xyz.adscope.amps.AMPSSDK:AMPSSDK:5.1.0.34")
    api("xyz.adscope.amps.AMPSSDK:AMPSSigmobAdapter:5.1.4240.0")
    api("xyz.adscope.amps.AMPSSDK:ASNPSDK:5.2.1.3")
    api("xyz.adscope.amps.AMPSSDK:Baidu_MobAds_SDK:release_v9.40")
    api("xyz.adscope.amps.AMPSSDK:beizi_fusion_sdk:5.2.1.21")
    api("xyz.adscope.amps.AMPSSDK:common:5.1.0.31")
    api("xyz.adscope.amps.AMPSSDK:GDTSDK.unionNormal:4.642.1512")
    api("xyz.adscope.amps.AMPSSDK:kssdk-ad:4.6.30.1-publishRelease-4e360ba1fe")
    api("xyz.adscope.amps.AMPSSDK:ms-sdk:2.5.6.6_release")
}