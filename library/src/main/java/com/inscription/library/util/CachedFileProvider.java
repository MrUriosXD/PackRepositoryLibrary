package com.inscription.library.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;

public class CachedFileProvider extends ContentProvider {
    public String authority;
    private UriMatcher uriMatcher;

    public static String getAuthority(Context context) {
        return context.getPackageName() + ".CachedFileProvider";
    }

    public static Uri getCacheFileUri(Context context, String file) {
        return Uri.parse("content://" + CachedFileProvider.getAuthority(context) + "/" + file);
    }

    @Override
    public boolean onCreate() {
        authority = getAuthority(getContext());
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, "*", 1);
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        // Otherwise unrecognised Uri
        if (uriMatcher.match(uri) == 1) {// If it returns 1 - then it matches the Uri defined in onCreate
            String fileLocation = getContext().getCacheDir() + File.separator + uri.getLastPathSegment();
            return ParcelFileDescriptor.open(new File(fileLocation), ParcelFileDescriptor.MODE_READ_ONLY);
        }
        throw new FileNotFoundException("Unsupported uri: " + uri.toString());
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentvalues, String s, String[] as) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] as) {
        return 0;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentvalues) {
        return null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String s, String[] as1, String s1) {
        return null;
    }
}