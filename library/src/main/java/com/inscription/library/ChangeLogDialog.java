package com.inscription.library;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
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

import static com.inscription.library.util.AppUtils.StyleDialogColor;

/**
 * Class to show a change log dialog
 */
public class ChangeLogDialog {

    /*
     * Class to show a license dialog
     *
     * Example xml
         <changelog>
             <release version="1.0.0.0" versioncode="1000" changeDate="00/00/0000">
                 <added>Initial Release.</added>
                 <update>Update UI</update>
                 <segurity>Bug fixes and other minor improvements.</segurity>
                 <removed>Removed Content</removed>
             </release>
         </changelog>
     */
    private static final String TAG = "ChangeLogDialog";

    private static final String CHANGELOG_XML = "changelog";

    //No modificar
    private final Context mContext;
    public ChangeLogDialog(final Context context) {mContext = context;}
    private Context getContext() {return mContext;}

    //CSS style for the html
    private String getStyle() {return String.format("<style type=\"text/css\">%s</style>", mStyle);}

    public void setStyle(final String style) {mStyle = style;}

    private String mStyle = "html, body { background-color: #FFFFFF; color: #303030; }"
                    +   "body { font-size: 12pt; }"
                    +   "h1 { font-size: 12pt; }"
                    +   "li { margin: 0px 0px 5px 0px; font-size: 10pt; list-style: none; }"
                    +   "ul { padding: 0px; }"
                    +   ".version { font-size: 12pt; font-weight: normal; }"
                    +   ".summary { font-size: 12pt; color: #606060; display: block; clear: left; }"
                    +   ".date { font-size: 12pt; color: #606060;  display: block; font-family: monospace; }"
                    +   ".green, .blue, .orange, .red, .gray { padding: .0rem .4rem; border-radius: .15rem; }"
                    +   ".green { background-color: #d1efd5; color: #4f6e33; }" 				/* Added */
                    +   ".blue { background-color: #b8d3ef; color: #506874;}"					/* Update */
                    +	".orange { background-color: #fdd9b5; color: #a77312; }" 				/* Changed */
                    +   ".red { background-color: #efd1d1; color: #a55468; }" 					/* Segurity*/
                    +   ".gray { background-color: #dadada; color: #6b6b6b; }"					/* Removed */
            ;

    /**
     * Contains constants for the release element of {@code changelog.xml}.
     */
    private interface ReleaseTag {
        String NAME = "release";
        String ATTRIBUTE_VERSION = "version";
        String ATTRIBUTE_VERSION_CODE = "versioncode";
        String ATTRIBUTE_DATE = "changeDate";
        String ATTRIBUTE_SUMMARY = "summary";
    }

    /**
     * Contains constants for the change element of {@code changelog.xml}.
     */
    interface ChangeTag {
        String ADDED = "added";
        String CHANGED = "changed";
        String UPDATE = "update";
        String REMOVE = "remove";
        String SEGURITY = "segurity";
    }

    //Get the current app version
    private String getAppVersion() {
        String versionName = "";
        try {
            final PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return versionName;
    }

    //Parse a date string from the xml and format it using the local date format
    @SuppressLint("SimpleDateFormat")
    private String parseDate(final String dateString) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            final Date parsedDate = dateFormat.parse(dateString);
            return DateFormat.getDateFormat(mContext).format(parsedDate);
        } catch (ParseException ignored) {
            //If there is a problem parsing the date just return the original string
            return dateString;
        }
    }

    //Parse a the release tag and appends it to the changelog builder
    private void parseReleaseTag (StringBuilder changelogBuilder, final XmlResourceParser xml) throws XmlPullParserException, IOException {

        //Add version and date if available
        changelogBuilder.append("<h1>").append(mContext.getResources().getString(R.string.version)).append(" ").append("<span class=\"version\">").append(xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_VERSION)).append(" (").append(parseDate(xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_DATE))).append(")").append("</span>").append("</h1>");

        //Add summary if available
        if (xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_SUMMARY) != null) {
            changelogBuilder.append("<span class=\"summary\">").append(xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_SUMMARY)).append("</span>");
        }

        changelogBuilder.append("<ul>");
        int eventType = xml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (xml.getName().equals(ChangeTag.ADDED))) {
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(ChangeTag.ADDED))) {
                eventType = xml.next();
                changelogBuilder.append("<li>").append("<span class=\"green\">").append(mContext.getResources().getString(R.string.changetag_added)).append("</span>").append(parseTextTag(xml)).append("</li>");
            }
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(ChangeTag.REMOVE))) {
                eventType = xml.next();
                changelogBuilder.append("<li>").append("<span class=\"red\">").append(mContext.getResources().getString(R.string.changetag_removed)).append("</span>").append(parseTextTag(xml)).append("</li>");
            }
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(ChangeTag.CHANGED))) {
                eventType = xml.next();
                changelogBuilder.append("<li>").append("<span class=\"orange\">").append(mContext.getResources().getString(R.string.changetag_changed)).append("</span>").append(parseTextTag(xml)).append("</li>");
            }
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(ChangeTag.UPDATE))) {
                eventType = xml.next();
                changelogBuilder.append("<li>").append("<span class=\"blue\">").append(mContext.getResources().getString(R.string.changetag_update)).append("</span>").append(parseTextTag(xml)).append("</li>");
            }
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(ChangeTag.SEGURITY))) {
                eventType = xml.next();
                changelogBuilder.append("<li>").append("<span class=\"gray\">").append(mContext.getResources().getString(R.string.changetag_security)).append("</span>").append(parseTextTag(xml)).append("</li>");
            }
            eventType = xml.next();
        }
        changelogBuilder.append("</ul>");
    }

    //Parse a function tag and return html code
    private String parseTextTag(final XmlPullParser xml) throws XmlPullParserException, IOException {
        StringBuilder changelogBuilder = new StringBuilder();
        int eventType = xml.getEventType();
        if (eventType == XmlPullParser.TEXT){
            changelogBuilder.append("&nbsp;").append(xml.getText());
        }
        eventType = xml.next();
        return changelogBuilder.toString();
    }

    //Get the changelog in html code, this will be shown in the dialog's webview
    private String getHTMLChangelog(final int aResourceId, final Resources resources, final int version) {
        boolean releaseFound = false;
        final StringBuilder changelogBuilder = new StringBuilder();
        changelogBuilder.append("<html><head>").append(getStyle()).append("</head><body>");
        try (XmlResourceParser xml = resources.getXml(aResourceId)) {
            //Parse child nodes //ChangeText
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(ReleaseTag.NAME))) {
                    //Check if the version matches the release tag.
                    //When version is 0 every release tag is parsed.
                    final int versioncode = Integer.parseInt(xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_VERSION_CODE));
                    if ((version == 0) || (versioncode == version)) {
                        parseReleaseTag(changelogBuilder, xml);
                        releaseFound = true; //At lease one release tag has been parsed.
                    }
                }
                eventType = xml.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage(), e);
            return "";
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return "";
        }
        changelogBuilder.append("</body></html>");

        //Check if there was a release tag parsed, if not return an empty string.
        if (releaseFound) {
            return changelogBuilder.toString();
        } else {
            return "";
        }
    }

    //Call to show the change log dialog
    public void show() { show(0); }

    private void show(final int version) {
        //Get resources
        final String packageName = mContext.getPackageName();
        final Resources resources;
        try {
            resources = mContext.getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException ignored) {
            return;
        }

        //Get dialog title
        String title = resources.getString(R.string.title_changelog);
        title = String.format("%s v%s", title, getAppVersion());

        //Get changelog xml resource id
        final int resID = resources.getIdentifier(CHANGELOG_XML, "xml", packageName);

        //Create html change log
        final String htmlChangelog = getHTMLChangelog(resID, resources, version);

        //Get button strings
        final String closeString = resources.getString(R.string.close);

        //Check for empty change log
        if (htmlChangelog.length() == 0) {
            //Could not load credits, message user and exit
            Toast.makeText(mContext, "Could not load" + CHANGELOG_XML, Toast.LENGTH_SHORT).show();
            return;
        }

        //Create web view and load html
        final WebView webView = new WebView(mContext);
        webView.loadDataWithBaseURL(null, htmlChangelog, "text/html", "utf-8", null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, StyleDialogColor)
                .setTitle(title)
                .setView(webView)
                .setPositiveButton(closeString, new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
}