package com.inscription.library.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.inscription.library.BuildConfig;
import com.inscription.library.R;

public class AppUtils{

    public static final int StyleDialogColor = R.style.AlertDialogTheme;
    public static final String appPackageName = BuildConfig.APPLICATION_ID;
    public static final String appUserName = "MrUriosXD";
    public static final String appUserNameID = "7725682870183374926";
    public static final String email = "mruriosxd@gmail.com";
    /**
     * Utility method for retrieving the appversion
     *
     * @param context context
     * @return the app version name
     */
    public static String GetAppVersion(Context context){
        try {
            PackageInfo _info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return _info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int GetVersionCode(Context context){
        try {
            PackageInfo _info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return _info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void uninstallApk(Context context) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + appPackageName));
        context.startActivity(intent);
    }

    /**
     * Returns the non-scaled pixel resolution of the current default display being used by the
     * WindowManager in the specified context.
     * @param context context to use to retrieve the current WindowManager
     * @return a string in the format "WxH", or the empty string "" if resolution cannot be determined
     */
    private static String getResolution(final Context context) {
        // user reported NPE in this method; that means either getSystemService or getDefaultDisplay
        // were returning null, even though the documentation doesn't say they should do so; so now
        // we catch Throwable and return empty string if that happens
        String resolution = "";

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        resolution = metrics.widthPixels + "x" + metrics.heightPixels;

        return resolution;
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private static String getAndroidVersion(int sdk) {
        switch (sdk) {
            case 1:  return  "Android 1.0"   + " (" + "1" + " " + "Apple Pie"           + ")";
            case 2:  return  "Android 1.1"   + " (" + "2" + " " + "Banana Bread"        + ")";
            case 3:  return  "Android 1.5"   + " (" + "3" + " " + "Cupcake"             + ")";
            case 4:  return  "Android 1.6"   + " (" + "4" + " " + "Donut"               + ")";
            case 5:  return  "Android 2.0"   + " (" + "5" + " " + "Eclair"              + ")";
            case 6:  return  "Android 2.0.1" + " (" + "6" + " " + "Eclair"              + ")";
            case 7:  return  "Android 2.1"   + " (" + "7" + " " + "Eclair"              + ")";
            case 8:  return  "Android 2.2"   + " (" + "8" + " " + "Froyo"               + ")";
            case 9:  return  "Android 2.3"   + " (" + "9" + " " + "Gingerbread"         + ")";
            case 10: return  "Android 2.3.3" + " (" + "10" + " " + "Gingerbread"         + ")";
            case 11: return  "Android 3.0"   + " (" + "11" + " " + "Honeycomb"           + ")";
            case 12: return  "Android 3.1"   + " (" + "12" + " " + "Honeycomb"           + ")";
            case 13: return  "Android 3.2"   + " (" + "13" + " " + "Honeycomb"           + ")";
            case 14: return  "Android 4.0"   + " (" + "14" + " " + "Ice Cream Sandwich"  + ")";
            case 15: return  "Android 4.0.3" + " (" + "15" + " " + "Ice Cream Sandwich"  + ")";
            case 16: return  "Android 4.1"   + " (" + "16" + " " + "Jelly Bean"          + ")";
            case 17: return  "Android 4.2"   + " (" + "17" + " " + "Jelly Bean"          + ")";
            case 18: return  "Android 4.3"   + " (" + "18" + " " + "Jelly Bean"          + ")";
            case 19: return  "Android 4.4"   + " (" + "19" + " " + "KitKat"              + ")";
            case 20: return  "Android 4.4"   + " (" + "20" + " " + "KitKat Watch"        + ")";
            case 21: return  "Android 5.0"   + " (" + "21" + " " + "Lollipop"            + ")";
            case 22: return  "Android 5.1"   + " (" + "22" + " " + "Lollipop"            + ")";
            case 23: return  "Android 6.0"   + " (" + "23" + " " + "Marshmallow"         + ")";
            case 24: return  "Android 7.0"   + " (" + "24" + " " + "Nougat"              + ")";
            case 25: return  "Android 7.1.1" + " (" + "25" + " " + "Nougat"              + ")";
            case 26: return  "Android 8.0"   + " (" + "26" + " " + "Oreo"                + ")";
            case 27: return  "Android 8.1"   + " (" + "27" + " " + "Oreo"                + ")";
            case 28: return  "Android 9.0"   + " (" + "28" + " " + "Pie"                 + ")";
            case 29: return  "Android 9.1"   + " (" + "29" + " " + "Pie"                 + ")";
            default: return  "";
        }
    }

}
