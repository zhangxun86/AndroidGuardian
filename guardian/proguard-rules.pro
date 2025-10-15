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



# AdScope聚合混淆
-dontwarn xyz.adscope.amps.**
-keep class xyz.adscope.amps.** {*; }
-dontwarn xyz.adscope.common.**
-keep class xyz.adscope.common.** {*; }

# AdScope广告渠道适配器混淆
-dontwarn xyz.adscope.amps.adapter.**
-keep class  xyz.adscope.amps.adapter.**{*;}

# 倍孜混淆，不接入bz sdk可以不引入
-dontwarn com.beizi.fusion.**
-dontwarn com.beizi.ad.**
-keep class com.beizi.fusion.** {*; }
-keep class com.beizi.ad.** {*; }

#优加混淆，不接入asnp sdk可以不引入
-dontwarn biz.beizi.adn.**
-keep class biz.beizi.adn.** {*; }


# 广点通混淆，不接入gdt sdk可以不引入
-keep class com.qq.e.** {
	public protected *;
}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-dontwarn  org.apache.**

# 穿山甲-GroMore融合版本广告渠道混淆 不接入csj sdk可以不引入
-keep class bykvm*.**
-keep class com.bytedance.msdk.adapter.**{ public *; }
-keep class com.bytedance.msdk.api.** {
 public *;
}


# 百度广告渠道混淆 不接入baidu sdk可以不引入
-ignorewarnings
-dontwarn com.baidu.mobads.sdk.api.**
-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}

-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

-keep class com.baidu.mobads.** { *; }
-keep class com.style.widget.** {*;}
-keep class com.component.** {*;}
-keep class com.baidu.ad.magic.flute.** {*;}
-keep class com.baidu.mobstat.forbes.** {*;}


# 快手广告渠道混淆 不接入ks sdk可以不引入
-keep class org.chromium.** {*;}
-keep class org.chromium.** { *; }
-keep class aegon.chrome.** { *; }
-keep class com.kwai.**{ *; }
-keep class com.yxcorp.kuaishou.addfp.android.Orange {*;}
-dontwarn com.kwai.**
-dontwarn com.kwad.**
-dontwarn com.ksad.**
-dontwarn aegon.chrome.**

#京东混淆 不接入jd sdk可以不引入
-keep class com.jd.ad.sdk.** { *; }

#Sigmob广告渠道混淆 不接入Sigmob sdk可以不引入
-keep class com.sigmob.sdk.**{ *;}
-keep interface com.sigmob.sdk.**{ *;}
-keep class com.sigmob.windad.**{ *;}
-keep interface com.sigmob.windad.**{ *;}
-keep class com.czhj.**{ *;}
-keep interface com.czhj.**{ *;}
-keep class com.tan.mark.**{*;}


# 美数广告渠道混淆 不接入美数 sdk可以不引入

# GSON
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { <fields>; }
-keep class com.google.gson.examples.android.model.** { <fields>; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
@com.google.gson.annotations.SerializedName <fields>;
}

# msad
-keep class com.meishu.sdk.** { *; }

# OAID
-keep class com.bun.miitmdid.** { *; }
-keep class com.bun.lib.** { *; }
-keep class com.asus.msa.** { *; }
-keep class com.huawei.hms.ads.identifier.** { *; }
-keep class com.netease.nis.sdkwrapper.** { *; }
-keep class com.samsung.android.deviceidservice.** { *; }
-keep class a.** { *; }
-keep class XI.** { *; }

# 章鱼混淆 不接入章鱼 sdk可以不引入
-dontwarn com.octopus.**
-keep class com.octopus.** {*;}

#========= Pangle / GroMore SDK Proguard Rules Start =========
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep class com.pgl.ssdk.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.core.IDownloadBusinessHelper {*;}
-keep public interface com.bytedance.sdk.openadsdk.mediation.bridge.custom.MediationCustomAd {*;}
#========= Pangle / GroMore SDK Proguard Rules End ===========