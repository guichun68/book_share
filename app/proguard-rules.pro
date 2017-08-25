# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\adt-bundle-windows-x86_64_20140101\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
##3D地图
#-keepclass com.amap.api.mapcore.**{*;}
#-keepclass com.amap.api.maps.**{*;}
#-keepclass com.autonavi.amap.mapcore.*{*;}
#
##定位
#-keepclass com.amap.api.location.**{*;}
#-keepclass com.loc.**{*;}
#-keepclass com.amap.api.fence.**{*;}
#-keepclass com.autonavi.aps.amapapi.model.**{*;}
#3D地图
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.amap.api.trace.**{*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}



#-keep class com.huawei.** { *; }
#-dontwarn com.huawei.**
#-keep class com.hianalytics.android.** {*;}