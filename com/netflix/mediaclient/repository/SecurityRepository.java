// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.repository;

import com.netflix.mediaclient.Log;
import android.content.pm.ApplicationInfo;
import android.content.Context;

public final class SecurityRepository
{
    private static final String BOOTLOADER_PARAMETER_CERTIFICATION_VERSION = "certification_version";
    private static final String BOOTLOADER_PARAMETER_DEVICE_CATEGORY = "device_cat";
    private static final String BOOTLOADER_PARAMETER_DEVICE_TYPE = "device_type";
    private static final String BOOTLOADER_PARAMETER_FULL_ESN = "e";
    private static final String BOOTLOADER_PARAMETER_OS = "os";
    private static final String BOOTLOADER_PARAMETER_SDK_VERSION = "sdk_version";
    private static final String BOOTLOADER_PARAMETER_SOFTWARE_VERSION = "sw_version";
    private static final String BOOTLOADER_PARAMETER_WEB_API_VERSION = "v";
    private static final String BOOTLOADER_URL = "https://www.netflix.com/applanding/android";
    private static final String BOOTLOADER_WEB_API_VERSION = "2.0";
    private static final int CONSTANT_CRITTERCISM_APP_ID = 2;
    private static final int CONSTANT_DEVICE_ID_TOKEN = 1;
    private static final int CONSTANT_FACEBOOK_ID = 0;
    private static final String ESN_DELIM = "-";
    private static final String MDXLIB_VERSION_VALUE = "2013.2";
    private static final String MODEL_DELIM = "_";
    public static final String NCCP_VERSION = "NCCP/2.15";
    private static final String NRDAPP_VERSION_VALUE = "2013.2";
    private static final String NRDLIB_VERSION_VALUE = "2013.2";
    public static final String SENDER_ID = "484286080282";
    private static final String TAG = "SEC";
    private static String crittercismAppId;
    private static String deviceIdToken;
    private static String esnPrefix;
    private static String facebookId;
    private static boolean sLoaded;
    
    public static String getBootloaderParameterCertificationVersion() {
        return "certification_version";
    }
    
    public static String getBootloaderParameterDeviceCategory() {
        return "device_cat";
    }
    
    public static String getBootloaderParameterDeviceType() {
        return "device_type";
    }
    
    public static String getBootloaderParameterFullEsn() {
        return "e";
    }
    
    public static String getBootloaderParameterOs() {
        return "os";
    }
    
    public static String getBootloaderParameterSdkVersion() {
        return "sdk_version";
    }
    
    public static String getBootloaderParameterSoftwareVersion() {
        return "sw_version";
    }
    
    public static String getBootloaderParameterWebApiVersion() {
        return "v";
    }
    
    public static String getBootloaderUrl() {
        return "https://www.netflix.com/applanding/android";
    }
    
    public static String getBootloaderWebApiVersion() {
        return "2.0";
    }
    
    public static String getCrittercismAppId() {
        return SecurityRepository.crittercismAppId;
    }
    
    public static String getDeviceIdToken() {
        return SecurityRepository.deviceIdToken;
    }
    
    public static String getEsnDelim() {
        return "-";
    }
    
    public static String getEsnPrefix() {
        return SecurityRepository.esnPrefix;
    }
    
    public static String getFacebookId() {
        return SecurityRepository.facebookId;
    }
    
    public static final int getLibraryVersion() {
        return native_getLibraryVersion();
    }
    
    public static String getMdxLibVersion() {
        return "2013.2";
    }
    
    public static String getModelDelim() {
        return "_";
    }
    
    private static String getNativeLibraryDirectory(final Context context) {
        final ApplicationInfo applicationInfo = context.getApplicationInfo();
        if ((applicationInfo.flags & 0x80) != 0x0 || (applicationInfo.flags & 0x1) == 0x0) {
            return applicationInfo.nativeLibraryDir;
        }
        return null;
    }
    
    public static String getNrdAppVersion() {
        return "2013.2";
    }
    
    public static String getNrdLibVersion() {
        return "2013.2";
    }
    
    public static boolean isLoaded() {
        return SecurityRepository.sLoaded;
    }
    
    public static boolean loadLibraries(final Context context) {
        boolean sLoaded = true;
        synchronized (SecurityRepository.class) {
            if (SecurityRepository.sLoaded) {
                Log.w("SEC", "We already loaded native libraries!");
            }
            else {
                final String nativeLibraryDirectory = getNativeLibraryDirectory(context);
                Label_0143: {
                    if (nativeLibraryDirectory == null) {
                        break Label_0143;
                    }
                    try {
                        Log.d("SEC", "Loading library from app file system. Installed or updated app.");
                        final String string = nativeLibraryDirectory + "/libnetflixmp_jni.so";
                        if (Log.isLoggable("SEC", 3)) {
                            Log.d("SEC", "Loading from " + string);
                        }
                        System.load(string);
                        SecurityRepository.sLoaded = true;
                        if (SecurityRepository.sLoaded) {
                            native_init(new byte[0]);
                            SecurityRepository.deviceIdToken = native_getConstant(1);
                            SecurityRepository.facebookId = native_getConstant(0);
                            SecurityRepository.crittercismAppId = native_getConstant(2);
                        }
                        sLoaded = SecurityRepository.sLoaded;
                        return sLoaded;
                        Log.d("SEC", "Loading library leaving to android to find mapping. Preloaded app.");
                        System.loadLibrary("netflixmp_jni");
                    }
                    catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                        Log.e("SEC", "Failed to load library from assumed location", unsatisfiedLinkError);
                    }
                }
            }
            return sLoaded;
        }
    }
    
    private static final native String native_getConstant(final int p0);
    
    private static final native int native_getLibraryVersion();
    
    private static final native void native_init(final byte[] p0);
}