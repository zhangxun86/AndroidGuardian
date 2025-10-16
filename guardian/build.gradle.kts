plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.zzsr.guardian"
    // 建议将 compileSdk 与 AGP 版本匹配，例如 34
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
        // 使用 Java 1.8 以获得更广泛的兼容性
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // --- 移除本地 AAR 依赖 ---
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    // --- 核心库依赖 ---
    implementation(libs.androidx.core.ktx)
    // 'api' 暴露 lifecycle-process 的 API 给使用者
    api(libs.androidx.lifecycle.process)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // --- 移除不必要的 UI 依赖 ---
    // implementation(libs.androidx.appcompat)
    // implementation(libs.material)

    // --- AMPS 广告 SDK 核心和适配器 (请根据官方文档确认版本号和路径) ---
    // 假设 AMPS SDK 发布在 MavenCentral 或其他公共仓库
    // 你需要找到它们的确切坐标，以下为示例格式
    implementation("xyz.adscope.amps:AMPSSDK:5.1.0.34")
    implementation("xyz.adscope.amps.adapter:AMPSASNPAdapter:5.1.5213.0")
    implementation("xyz.adscope.amps.adapter:AMPSBZAdapter:5.1.52121.0")
    implementation("xyz.adscope.amps.adapter:AMPSGDTAdapter:5.1.46421512.0")
    implementation("xyz.adscope.amps.adapter:AMPSKSAdapter:5.1.46301.1")
    implementation("xyz.adscope.amps.adapter:AMPSCSJAdapter:5.1.7012.0")
    implementation("xyz.adscope.amps.adapter:AMPSGMAdapter:5.1.7012.0")
    implementation("xyz.adscope.amps.adapter:AMPSBDAdapter:5.1.940.0")
    implementation("xyz.adscope.amps.adapter:AMPSQMAdapter:5.1.347310439.0")
    implementation("xyz.adscope.amps.adapter:AMPSSigmobAdapter:5.1.4240.0")
    implementation("xyz.adscope.amps.adapter:AMPSJDAdapter:5.1.2632.0")
    implementation("xyz.adscope.amps.adapter:AMPSHWAdapter:5.1.13478301.0")
    implementation("xyz.adscope.amps.adapter:AMPSOCTAdapter:5.1.1641.1")
    implementation("xyz.adscope.amps.adapter:AMPSMSAdapter:5.1.2566.4")
    // implementation("xyz.adscope:common_v2:...") // 公共库可能也需要添加

    // --- 各个第三方广告平台的基础 SDK ---
    // ASNPSDK / beizi_fusion_sdk
    implementation("biz.beizi.adn:ASNPSDK:5.2.1.3")
    implementation("biz.beizi.adn:beizi_fusion_sdk:5.2.1.21")

    // 广点通 (GDT)
    implementation("com.qq.e.union:union:4.642.1512")

    // 穿山甲 (Pangle / CSJ / GroMore)
    implementation("com.pangle.cn:ads-sdk-gromore:7.0.1.2") // open_ad_sdk_7.0.1.2.aar 对应的是 gromore

    // 快手 (KS)
    implementation("com.kwad.sdk:kssdk-ad:4.6.30.1")

    // 百度 (Baidu)
    implementation("com.baidu.mobads:Baidu_MobAds_SDK:9.40")

    // 京东 (JD)
    implementation("com.github.JAD-FE-TEAM.JADYunAndroid:jad_yun_sdk:2.6.28")

    // 华为 (Huawei)
    implementation("com.huawei.hms:ads-lite:13.4.78.301")
    implementation("com.huawei.hms:ads-consent:3.4.78.301")

    // Sigmob (wind-sdk)
    implementation("com.sigmob.windad:wind-sdk:4.24.0")

    // 章鱼 (Octopus)
    implementation("com.octopus.ad:octopus_ad_sdk:1.6.4.1")

    // 美数 (Meishu)
    implementation("com.meishu.sdk:ms-sdk:2.5.6.6")

    // 趣盟 (Qumeng)
    implementation("com.qumeng.adv:qumeng:3.473.10.439")


    // --- 测试依赖 ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}