package com.inscription.library;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static com.inscription.library.util.AppUtils.StyleDialogColor;

public class WhatsNewDialog extends ChangeLogDialog {
    private static final String WHATS_NEW_LAST_SHOWN = "whats_new_last_shown";

    public WhatsNewDialog(final Context context) {
        super(context);
    }

    //Get the current app version
    private int getAppVersionCode() {
        try {
            final PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
            return 0;
        }
    }

    public void forceShow() {
        //Show only the changes from this version (if available)
        show(getAppVersionCode());
    }

    @Override
    public void show() {
        //ToDo check if version is shown
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final int versionShown = prefs.getInt(WHATS_NEW_LAST_SHOWN, 0);
        final int appVersionCode = getAppVersionCode();
        if (versionShown != appVersionCode) {
            //This version is new, show only the changes from this version (if available)
            show(appVersionCode);

            //Update last shown version
            final SharedPreferences.Editor edit = prefs.edit();
            edit.putInt(WHATS_NEW_LAST_SHOWN, appVersionCode);
            edit.commit();
        }
    }

}
