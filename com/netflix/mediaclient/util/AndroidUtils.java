// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.util;

import android.media.AudioManager;
import android.app.PendingIntent;
import com.netflix.mediaclient.ui.launch.RelaunchActivity;
import android.app.AlarmManager;
import com.netflix.mediaclient.service.error.action.RestartApplicationAction;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.content.ComponentName;
import com.netflix.mediaclient.service.pservice.PServiceWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.util.DisplayMetrics;
import android.support.v4.content.ContextCompat;
import com.netflix.mediaclient.javabridge.transport.NativeTransport;
import com.netflix.mediaclient.NetflixApplication;
import com.netflix.mediaclient.service.logging.error.ErrorLoggingManager;
import android.app.Activity;
import com.netflix.mediaclient.repository.SecurityRepository;
import com.netflix.mediaclient.media.PlayerType;
import com.netflix.mediaclient.service.configuration.PlayerTypeFactory;
import android.support.v4.os.EnvironmentCompat;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.content.ContextWrapper;
import android.text.format.Formatter;
import android.os.StatFs;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.security.NoSuchAlgorithmException;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Base64;
import java.security.MessageDigest;
import android.os.Build$VERSION;
import android.os.Process;
import android.os.StrictMode$VmPolicy$Builder;
import android.os.StrictMode;
import android.os.StrictMode$ThreadPolicy$Builder;
import java.io.IOException;
import android.os.Debug;
import android.os.Environment;
import android.graphics.Bitmap;
import com.netflix.mediaclient.Log;
import java.io.File;
import android.content.Context;

public final class AndroidUtils
{
    public static final String FILENAME = "FILENAME";
    private static final String MSL_APPBOOT_NFANDROID1 = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA1h/UVRYyyDMlfY4eEiGTAYH8enFcyOaAyW3ulv1X/lCZL8pdk4KywDxssdhvkj8ib7vrrK8ktf/aeTxzezR6HVcS9JZ9kIfTgGrTVnUHFjcYOBdPAaeJl7Mx8+ubAlYeMwsOaG/nHD96/7RkqxF0+FB4RKZTnfjTziZaIEkmLLb+ifyB5mvk8SVa7i8qJf2Dw2l/0uxp93558Dl9xeAOH3Hz3G7wgtxnL71BSOE8H9s7z4etQmuSdf++n++C9HeszauVohHhgtejw+qqf63a8R9/6MeJwh/VRJTw7nXM2PN+8ERpQzJR+AWOkHqbC2mgvSYEFMtBhodOxnp73bR7LMIAbObrTm7VDQBcav5wWlH+KPCaBR0VCRSy9GG23CHOsuWVln2idnDz/zFBHnVnWKVAanZ3Ot4LmM2nrJvSlrt1OiQSLaI+CJHO8InfVTQEXpduoiGkLpc1HcmWNF98JuA8ZX3tqmcncdHnEMG3A5hZVnM6MrsidcQTsojl/MuoXrMeuWkvQQUe4wklBHleLs6jA/Au0oT4Q34luCvG3C32N2XiUcAeFdGd3MuDlDjqG88A9CLd21eh1HqkHD76qeWGnwumLHyJmqL25Lmz4LMjJ/nkXaY9r4Fya2/I/aV9kt5lCaPY3Wb4nDivjPqM6iP9vHCKOMxwjvbE4DFgN60CAwEAAQ==";
    private static final String MSL_APPBOOT_NFANDROIDD = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAniUdnoFyqHUerpq3zqrStDVAdmDXg90ARWUjelG8L/JYmLC/z1AxlFoqcbwvcrLonaguZVW1WH8cZzl2EQGDMeydZyDq2zTNh+2mVvrPxiqoVx9rVOtQScJzxVYvbkcgxHEwQJMz1DsM+42bjuOsruNshvTCm+eq6he8SwvCGV4ny0pe/jwY+JZcO+CxTw7/zEHrn9nQo/8doOU8DaMrC20KaW0ss/R3dj5ofonouyRInr1nwpFPZzZvFf9lMJnxS0com8RDnTQpe2GsKt7HMl6p9eQiPCNXy8ACTD9kEwbM5WZoUj5T3eTY4VgCL7HTXZ09wta7M9utfHt3rvMctxCSrzR02SaSPA5LxnW0rzM9KYabZs+77wvXo1I0Cf6+pjWqsJjwhIYEQMlHw8q4l/I/CPdLNlqPH2KplapDGvZSTF1znTiQRowK3U65GJBb+Qdr1GBbVf+pYWRldujhW1+iU+wp8B4BFInLwLUgc/iFi/gN9xSWMDJiw79C02L59542l4CvsTJdAoNOZvdvEUxrpWS+ewP1y73fJvqX5Po5Hqm+h97Rg3ABVQ48lh5N8hSFB4gUX343QzxP/wT6keoCfDAzn99tutavJRExaboF32CJjA4yopgurkYUO5YgQigGiiV38Nrv2x8Aa0QX6+XhRmKksV6z90t/4mu9OQkCAwEAAQ==";
    public static final String OUTRES = "OUTRES";
    private static final String TAG = "nf_utils";
    public static final boolean debug = false;
    public static final boolean enableTestServer = false;
    public static final boolean isReleaseForConfigServer = true;
    
    public static boolean canDisplayTransparentWebpImages() {
        return getAndroidVersion() >= 18;
    }
    
    public static void clearApplicationData(final Context context) {
        final File file = new File(context.getCacheDir().getParent());
        if (file.exists()) {
            final String[] list = file.list();
            if (list != null && list.length > 0) {
                for (int length = list.length, i = 0; i < length; ++i) {
                    final String s = list[i];
                    if (!s.equals("lib")) {
                        deleteDir(new File(file, s));
                        if (Log.isLoggable()) {
                            Log.i("TAG", "File /data/data/com.netflix.mediaclient/" + s + " DELETED");
                        }
                    }
                }
            }
        }
    }
    
    public static Bitmap createScaledBitmapWithAspectRatio(final Bitmap bitmap, final int n, final int n2, final boolean b) {
        ThreadUtils.assertNotOnMain();
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        if (width > 0 && height > 0 && n > 0 && n2 > 0) {
            int n3;
            int n4;
            if (width > height) {
                n3 = n2 * height / width;
                n4 = n;
            }
            else {
                n4 = width * n2 / height;
                n3 = n2;
            }
            Log.i("nf_utils", "originalW=%d originalH=%d dstWidth=%d dstHeight=%d newWidth=%d newHeight=%d", width, height, n, n2, n4, n3);
            return Bitmap.createScaledBitmap(bitmap, n4, n3, b);
        }
        return bitmap;
    }
    
    public static boolean deleteDir(final File file) {
        if (file != null && file.isDirectory()) {
            final String[] list = file.list();
            if (list != null && list.length > 0) {
                for (int i = 0; i < list.length; ++i) {
                    if (!deleteDir(new File(file, list[i]))) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
    
    public static int dipToPixels(final Context context, final int n) {
        return (int)(context.getResources().getDisplayMetrics().density * n + 0.5f);
    }
    
    public static void dumpHprofToDisk() {
        final File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        externalStoragePublicDirectory.mkdirs();
        final String absolutePath = new File(externalStoragePublicDirectory, "netflix.prof").getAbsolutePath();
        try {
            Log.v("nf_utils", "************************************************************");
            Log.v("nf_utils", "Dumping HPROF profile to file...");
            Log.v("nf_utils", "************************************************************");
            Debug.dumpHprofData(absolutePath);
            Log.v("nf_utils", "************************************************************");
            Log.v("nf_utils", "Dump complete.  File: " + absolutePath);
            Log.v("nf_utils", "************************************************************");
        }
        catch (IOException ex) {
            Log.handleException("nf_utils", ex);
        }
    }
    
    public static void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode$ThreadPolicy$Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode$VmPolicy$Builder().detectAll().penaltyLog().build());
    }
    
    public static void forceStop(final Context context) {
        unmuteAudio(context);
        final int myPid = Process.myPid();
        if (Log.isLoggable()) {
            Log.d("nf_utils", "Destroying app proces " + myPid + "...");
        }
        Process.killProcess(myPid);
        if (Log.isLoggable()) {
            Log.d("nf_utils", "Destroying app proces " + myPid + " done.");
        }
    }
    
    public static int getAndroidVersion() {
        return Build$VERSION.SDK_INT;
    }
    
    public static String[] getAppSignatures(final Context context) {
        final String[] array = null;
        final String[] array2 = null;
        final String[] array3 = null;
        final String[] array4 = null;
        int n = 0;
        final PackageManager packageManager = context.getPackageManager();
        String[] array5;
        if (packageManager == null) {
            Log.e("nf_utils", "Package manager not found, this should NOT happen");
            array5 = array4;
        }
        else {
            String[] array6 = array;
            String[] array7 = array2;
            String[] array8 = array3;
            try {
                final PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 64);
                array6 = array;
                array7 = array2;
                array8 = array3;
                if (Log.isLoggable()) {
                    array6 = array;
                    array7 = array2;
                    array8 = array3;
                    Log.d("nf_utils", "Found # signatures: " + packageInfo.signatures.length);
                }
                array6 = array;
                array7 = array2;
                array8 = array3;
                final String[] array9 = new String[packageInfo.signatures.length];
                while (true) {
                    array5 = array9;
                    array6 = array9;
                    array7 = array9;
                    array8 = array9;
                    if (n >= packageInfo.signatures.length) {
                        break;
                    }
                    array6 = array9;
                    array7 = array9;
                    array8 = array9;
                    final MessageDigest instance = MessageDigest.getInstance("SHA");
                    array6 = array9;
                    array7 = array9;
                    array8 = array9;
                    instance.update(packageInfo.signatures[n].toByteArray());
                    array6 = array9;
                    array7 = array9;
                    array8 = array9;
                    array9[n] = new String(Base64.encode(instance.digest(), 0));
                    array6 = array9;
                    array7 = array9;
                    array8 = array9;
                    if (Log.isLoggable()) {
                        array6 = array9;
                        array7 = array9;
                        array8 = array9;
                        Log.d("nf_utils", "hash key[" + n + "]:" + array9[n]);
                    }
                    ++n;
                }
            }
            catch (PackageManager$NameNotFoundException ex) {
                Log.e("nf_utils", "Name not found", (Throwable)ex);
                return array6;
            }
            catch (NoSuchAlgorithmException ex2) {
                Log.e("nf_utils", "No such an algorithm", ex2);
                return array7;
            }
            catch (Exception ex3) {
                Log.e("nf_utils", "Error while getting signature", ex3);
                return array8;
            }
        }
        return array5;
    }
    
    public static long getAvailableInternalMemory() {
        final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return statFs.getAvailableBlocks() * statFs.getBlockSize();
    }
    
    public static String getAvailableInternalMemoryAsString(final Context context) {
        return Formatter.formatFileSize(context, getAvailableInternalMemory());
    }
    
    public static ClassLoader getClassLoader(final Class clazz) {
        final ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader != null) {
            return classLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }
    
    public static <T> T getContextAs(final Context context, final Class<T> clazz) {
        if (clazz.isInstance(context)) {
            return clazz.cast(context);
        }
        if (context instanceof ContextWrapper) {
            final Context baseContext = ((ContextWrapper)context).getBaseContext();
            if (baseContext != context) {
                return (T)getContextAs(baseContext, (Class<Object>)clazz);
            }
        }
        return null;
    }
    
    public static int getDimensionInDip(final Context context, final int n) {
        return (int)(context.getResources().getDimension(n) / context.getResources().getDisplayMetrics().density);
    }
    
    public static File getExternalDownloadDirIfAvailable(final Context context) {
        if (context != null && isExternalStorageAvailable()) {
            return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        }
        return null;
    }
    
    public static long getFreeSpaceOnFileSystem(final File file) {
        try {
            if (file.exists()) {
                return new StatFs(file.getPath()).getAvailableBytes();
            }
        }
        catch (IllegalArgumentException ex) {}
        return 0L;
    }
    
    public static String getHeapSizeString(final Context context) {
        return Formatter.formatShortFileSize(context, Runtime.getRuntime().maxMemory());
    }
    
    public static int getIntegerRes(final Context context, final int n) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getInteger(n);
    }
    
    public static String getMslAppBootKey() {
        return "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA1h/UVRYyyDMlfY4eEiGTAYH8enFcyOaAyW3ulv1X/lCZL8pdk4KywDxssdhvkj8ib7vrrK8ktf/aeTxzezR6HVcS9JZ9kIfTgGrTVnUHFjcYOBdPAaeJl7Mx8+ubAlYeMwsOaG/nHD96/7RkqxF0+FB4RKZTnfjTziZaIEkmLLb+ifyB5mvk8SVa7i8qJf2Dw2l/0uxp93558Dl9xeAOH3Hz3G7wgtxnL71BSOE8H9s7z4etQmuSdf++n++C9HeszauVohHhgtejw+qqf63a8R9/6MeJwh/VRJTw7nXM2PN+8ERpQzJR+AWOkHqbC2mgvSYEFMtBhodOxnp73bR7LMIAbObrTm7VDQBcav5wWlH+KPCaBR0VCRSy9GG23CHOsuWVln2idnDz/zFBHnVnWKVAanZ3Ot4LmM2nrJvSlrt1OiQSLaI+CJHO8InfVTQEXpduoiGkLpc1HcmWNF98JuA8ZX3tqmcncdHnEMG3A5hZVnM6MrsidcQTsojl/MuoXrMeuWkvQQUe4wklBHleLs6jA/Au0oT4Q34luCvG3C32N2XiUcAeFdGd3MuDlDjqG88A9CLd21eh1HqkHD76qeWGnwumLHyJmqL25Lmz4LMjJ/nkXaY9r4Fya2/I/aV9kt5lCaPY3Wb4nDivjPqM6iP9vHCKOMxwjvbE4DFgN60CAwEAAQ==";
    }
    
    public static PackageInfo getPackageInfo(final Context context, final String s) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }
        if (s == null) {
            throw new IllegalArgumentException("App name cannot be null!");
        }
        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(s, 0);
            if (packageInfo == null) {
                return null;
            }
            return packageInfo;
        }
        catch (PackageManager$NameNotFoundException ex) {
            return null;
        }
    }
    
    public static Bitmap getRoundedCornerBitmap(final Bitmap bitmap, final int n, final int n2, final int n3) {
        if (n3 == 0) {
            return bitmap;
        }
        final Bitmap bitmap2 = Bitmap.createBitmap(n, n2, Bitmap$Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap2);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, n, n2);
        final RectF rectF = new RectF(rect);
        final float n4 = n3;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, n4, n4, paint);
        paint.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.SRC_IN));
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, paint);
        return bitmap2;
    }
    
    public static StatFs getStatFsForExternalStorageDir(final File file) {
        try {
            if (file.exists() && "mounted".equals(EnvironmentCompat.getStorageState(file))) {
                return new StatFs(file.getPath());
            }
            goto Label_0071;
        }
        catch (Exception ex) {
            LogUtils.reportErrorSafely("getStatFsForExternalStorageDir " + file.getAbsolutePath() + " exception " + ex);
        }
        catch (IllegalArgumentException ex2) {
            goto Label_0071;
        }
    }
    
    public static String getUserAgent(final Context context) {
        final String version = AndroidManifestUtils.getVersion(context);
        final int versionCode = AndroidManifestUtils.getVersionCode(context);
        PlayerType playerType;
        if ((playerType = PlayerTypeFactory.getCurrentType(context)) == null) {
            Log.e("nf_utils", "This should not happen, player type was null at this point! Use default.");
            playerType = PlayerTypeFactory.findDefaultPlayerType();
        }
        final String mapPlayerTypeForLogging = PlayerType.mapPlayerTypeForLogging(playerType);
        final StringBuilder sb = new StringBuilder();
        sb.append("Netflix/").append(SecurityRepository.getNrdLibVersion());
        sb.append(' ').append("NCCP/2.15");
        sb.append(" (DEVTYPE=").append(version).append("-").append(versionCode);
        sb.append(' ').append("R").append(' ').append(SecurityRepository.getNrdLibVersion());
        sb.append(" android-").append(getAndroidVersion()).append('-');
        sb.append(mapPlayerTypeForLogging).append(" ; CERTVER=0)");
        return sb.toString();
    }
    
    public static boolean isActivityFinishedOrDestroyed(final Context context) {
        if (context == null) {
            return true;
        }
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity)context;
        }
        else {
            activity = getContextAs(context, Activity.class);
        }
        if (activity == null) {
            ErrorLoggingManager.logHandledException("No activity found in this context : " + context);
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }
    
    public static boolean isAndroid6AndHihger() {
        return getAndroidVersion() > 22;
    }
    
    public static boolean isAppInstalled(final Context context, final String s) {
        boolean b = true;
        final PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return false;
        }
        try {
            packageManager.getPackageInfo(s, 1);
            return b;
        }
        catch (PackageManager$NameNotFoundException ex) {
            b = false;
            return b;
        }
    }
    
    public static boolean isApplicationInBackground(final Context context) {
        return !isApplicationInForeground(context);
    }
    
    public static boolean isApplicationInForeground(final Context context) {
        return context != null && ((NetflixApplication)context.getApplicationContext()).getUserInput().isApplicationInForeground();
    }
    
    public static boolean isExternalStorageAvailable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }
    
    public static boolean isHd() {
        return NativeTransport.isHdCapable();
    }
    
    public static boolean isNetflixPreloaded(final Context context) {
        boolean b = false;
        final PackageManager packageManager = context.getPackageManager();
        try {
            final PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if ((packageInfo.applicationInfo.flags & 0x1) != 0x0 || (packageInfo.applicationInfo.flags & 0x80) != 0x0) {
                b = true;
            }
            if (Log.isLoggable()) {
                Log.d("nf_utils", "Netflix Stub/App present in the system folder ?=" + b + " ApplicationInfo.flags=" + packageInfo.applicationInfo.flags);
            }
            return b;
        }
        catch (PackageManager$NameNotFoundException ex) {
            return false;
        }
    }
    
    public static boolean isOpenMaxALSupportMainprofile() {
        return NativeTransport.isOMXALmpCapable();
    }
    
    public static boolean isOpenMaxALSupported() {
        return getAndroidVersion() > 13;
    }
    
    public static boolean isPermissionNotGranted(final Context context, final String s) {
        return ContextCompat.checkSelfPermission(context, s) != 0;
    }
    
    public static boolean isPropertyStreamingVideoDrs() {
        return NativeTransport.isPropertyStreamingVideoDrs();
    }
    
    public static boolean isScreenResolutionSameOrMore(final Activity activity, final int n, final int n2) {
        if (activity == null || activity.getWindowManager() == null) {
            Log.e("nf_utils", "Unable to get window manager! It should not happen!");
        }
        else {
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            if (Log.isLoggable()) {
                Log.d("nf_utils", "Given size (w,h): " + n + ", " + n2);
                Log.d("nf_utils", "Screen size (w,h): " + displayMetrics.widthPixels + ", " + displayMetrics.heightPixels);
            }
            if (displayMetrics.widthPixels < n || displayMetrics.heightPixels < n2) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isUnderTest() {
        try {
            Class.forName("com.netflix.mediaclient.test.NetflixTestRunner");
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public static boolean isWidgetInstalled(final Context context) {
        while (true) {
            final boolean b = true;
            final boolean b2 = false;
            try {
                if (AppWidgetManager.getInstance(context) != null) {
                    final int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, (Class)PServiceWidgetProvider.class));
                    if (Log.isLoggable()) {
                        Log.d("nf_utils", String.format("found widget: %b, num widgets installed: %d", appWidgetIds.length > 0, appWidgetIds.length));
                    }
                    return appWidgetIds.length > 0 && b;
                }
            }
            catch (Exception ex) {
                final boolean b3 = b2;
                if (Log.isLoggable()) {
                    Log.e("nf_utils", "AppWidgetManager dead? " + ex);
                    return false;
                }
                return b3;
            }
            return false;
        }
    }
    
    public static void logDeviceDensity(final Activity activity) {
        if (!Log.isLoggable() || activity == null) {
            return;
        }
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Log.d("nf_utils", "Logical density: " + displayMetrics.density);
        Log.d("nf_utils", "DPI density: " + displayMetrics.densityDpi);
        switch (displayMetrics.densityDpi) {
            default: {
                Log.d("nf_utils", "Uknown screen density!");
            }
            case 120: {
                Log.d("nf_utils", "ldpi - 120 pixels/inch; dpi scale = .75 (4 dpi = 3 pixels)");
            }
            case 160: {
                Log.d("nf_utils", "mdpi - 160 pixels/inch; dpi scale = 1 (1 dpi = 1 pixel)");
            }
            case 240: {
                Log.d("nf_utils", "hdpi - 240 pixels/inch; dpi scale = 1.5 (2 dpi = 3 pixels)");
            }
            case 213: {
                Log.d("nf_utils", "tvhdpi - 213 pixels/inch; dpi scale = 1.33 (1 dpi = 3 pixels)");
            }
            case 320: {
                Log.d("nf_utils", "xhdpi - 320 pixels/inch; dpi scale = 2 (1 dpi = 2 pixels)");
            }
            case 480: {
                Log.d("nf_utils", "xxhdpi - 480 pixels/inch; dpi scale = 3 (1 dpi = 3 pixels)");
            }
        }
    }
    
    public static ResolveInfo queryIntentActivities(final Context context, final Intent intent) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }
        if (intent == null) {
            throw new IllegalArgumentException("Intent cannot be null!");
        }
        try {
            return context.getPackageManager().resolveActivity(intent, 0);
        }
        catch (Throwable t) {
            return null;
        }
    }
    
    public static void restartApplication(final Activity activity, final String s) {
        new RestartApplicationAction(activity).run();
        final AlarmManager alarmManager = (AlarmManager)activity.getSystemService("alarm");
        if (alarmManager != null) {
            alarmManager.set(1, System.currentTimeMillis() + 2000L, PendingIntent.getActivity((Context)activity, 0, RelaunchActivity.createStartIntent(activity, s), 268435456));
        }
    }
    
    public static void setWindowSecureFlag(final Activity activity) {
        if (activity != null && getAndroidVersion() > 18) {
            activity.getWindow().addFlags(8192);
        }
    }
    
    private static void unmuteAudio(final Context context) {
        final AudioManager audioManager = (AudioManager)context.getSystemService("audio");
        if (audioManager != null) {
            audioManager.setStreamMute(3, false);
            Log.d("nf_utils", "UN-MUTED");
        }
    }
}
