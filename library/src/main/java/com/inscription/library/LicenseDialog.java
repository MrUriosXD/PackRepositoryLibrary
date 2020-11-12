package com.inscription.library;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static com.inscription.library.util.AppUtils.StyleDialogColor;

public class LicenseDialog {
    /*
	* Class to show a license dialog
	*
	* Example xml
		<licenses>
			<license>
				<website><url title="Titulo Libreria">https://github.com/autor/NameLibrary</url></website>
				<copyright>Copyright 2019 Nombre del Creador</copyright>
				<type>License.APACHE</type>
			</license>
		</licenses>
	*/
    private static final String TAG = "LicensesDialog";

    private static final String LICENSES_XML = "licenses";

    //Get dialog title
    private int mTitle = R.string.title_licenses;

    //No modificar
    private final Context mContext;

    public LicenseDialog(final Context context) {
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    //CSS style for the html
    private String getStyle() {
        return String.format("<style type=\"text/css\">%s</style>", mStyle);
    }

    public void setStyle(final String style) {
        mStyle = style;
    }

    private String mStyle = "html, body { background-color: #FFFFFF; color: #303030; }"
                        +   "body { font-size: 12pt; }"
                        +   "h1 { font-size: 12pt; }"
                        +   "li { margin: 0px 0px 5px 0px; font-size: 10pt; list-style: none; }"
                        +   "ul { padding: 0px; }"
                        +   "a { color: #000; text-decoration: none;  }"
                        +   "a:hover { color: #000; text-decoration: underline; }"
    ;

    // and we can set a custom Title, default is "credits"
    private int getCustomTitle() {
        return mTitle ;
    }

    public void setCustomTitle(final int title) {
        mTitle = title;
    }

    private interface LicenseTag {
        String NAME = "license";
        String WEBSITE = "website";
        String ATTRIBUTE_TITLE = "title";
        String URL = "url";
        String COPYRIGHT = "copyright";
        String TYPE = "type";
    }

    private void parseLicensesTag (StringBuilder licensesBuilder, final XmlResourceParser xml) throws XmlPullParserException, IOException {
        licensesBuilder.append("<ul>");
        int eventType = xml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (xml.getName().equals(LicenseTag.WEBSITE))) {
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(LicenseTag.WEBSITE))) {
                eventType = xml.next();
                licensesBuilder.append("<li><b>").append(parseUrlTag(xml)).append("</b></li>");
            }
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(LicenseTag.COPYRIGHT))) {
                eventType = xml.next();
                licensesBuilder.append("<li>").append(parseTextTag(xml)).append("</li>");
            }
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(LicenseTag.TYPE))) {
                eventType = xml.next();
                licensesBuilder.append("<li>").append(parseTextTag(xml)).append("</li>");
            }
            eventType = xml.next();
        }
        licensesBuilder.append("</ul>");
    }

    //Parse the url tag and return html code
    private String parseUrlTag(final XmlResourceParser xml) throws XmlPullParserException, IOException {
        int eventType = xml.getEventType();
        String url = "";
        String title = "";
        if (xml.getAttributeValue(null, "title") != null)
            title = xml.getAttributeValue(null, "title");
        while (eventType != XmlPullParser.END_TAG) {
            if (eventType == XmlPullParser.TEXT){
                url = xml.getText();
            }
            eventType = xml.next();
        }
        if (url.equals(""))
            return "";
        if (title.equals(""))
            title = url;
        return String.format("<a href ='%1$s'>%2$s</a>", url, title);
    }

    //Parse a function tag and return html code
    private String parseTextTag(final XmlPullParser xml) throws XmlPullParserException, IOException {
        StringBuilder changelogBuilder = new StringBuilder();
        int eventType = xml.getEventType();
        if (eventType == XmlPullParser.TEXT){
            changelogBuilder.append(xml.getText());
        }
        eventType = xml.next();
        return changelogBuilder.toString();
    }

    @SuppressLint("NewApi")
    //Get the credits in html code, this will be shown in the dialog's webview
    private String getHTMLLicense(int resID, final Resources resources) {
        final StringBuilder licensesBuilder = new StringBuilder();
        licensesBuilder.append("<html><head>").append(getStyle()).append("</head><body>");
        try (XmlResourceParser xml = resources.getXml(resID)) {
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(LicenseTag.NAME))) {
                    parseLicensesTag(licensesBuilder, xml);
                }
                eventType = xml.next();
            }
        }
        catch (final XmlPullParserException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        catch (final IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        licensesBuilder.append("</body></html>");
        return licensesBuilder.toString();
    }

    //Call to show the credits dialog
    public void show() {
        //Get resources
        final String packageName = mContext.getPackageName();
        Resources resources;
        try {
            resources = mContext.getPackageManager().getResourcesForApplication(packageName);
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }
        //Get credits xml resource id
        final int resID = resources.getIdentifier(LICENSES_XML, "xml", packageName);
        //Create html credits
        final String htmlLicense = getHTMLLicense(resID, resources);

        //Get button strings
        final String close = resources.getString(R.string.close);

        //Check for empty credits
        if (htmlLicense.equals("")) {
            //Could not load credits, message user and exit
            Toast.makeText(mContext, "Could not load" + " " + LICENSES_XML, Toast.LENGTH_SHORT).show();
            return;
        }
        //Create webview and load html
        final WebView WebView = new WebView(mContext);
        WebView.loadData(htmlLicense, "text/html", "utf-8");

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, StyleDialogColor)
                .setTitle(getCustomTitle())
                .setView(WebView)
                .setPositiveButton(close, new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
}
