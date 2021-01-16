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

public class ThanksDialog {
/*
 * Class to show a credits dialog
 *
 * Example xml
	<thanks>
	    <section>
		    <function title="Created by" author="Martin van Zuilekom" />
		    <function title="Special thanks to" author="">
		        <credit>Hameno</credit>
		        <credit>Pepyakin</credit>
			</function>
		</section>
	</thanks>
 */
    private static final String TAG = "ThanksDialog";

    private static final String TRANKS_XML = "thanks";

    private int mTitle = R.string.title_tranks;

    //No modificar
    private final Context mContext;
    public ThanksDialog(final Context context) {
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
    private String mStyle =
        "html, body { background-color: #FFFFFF; color: #303030; }"
    +   "body { font-size: 9pt; padding-top:10px; padding-left:15px; font-family: 'Roboto', 'Helvetica Neue', sans-serif; }"
    +   "h1 { font-size: 12pt; }"
    +   "span { font-size: 12pt; }"
    +   "li { margin: 0px; font-size: 10pt; }"
    +   "ul { padding-left: 30px; }"
    +   ".title { display:block; width:100%; text-align: center; font-size: 16px; }"
    ;
    // and we can set a custom Title, default is "credits"
    private int getCustomTitle() {
        return mTitle ;
    }

    public void setCustomTitle(final int title) {
        mTitle = title;
    }
    /**
     * Contains constants for the release element of {@code changelog.xml}.
     */
    private interface SectionTag {
        String NAME = "section";
        String FUNCTION = "function";
        String CREDIT = "credit";
        String ATTRIBUTE_TITLE = "title";
        String ATTRIBUTE_AUTHOR = "author";
    }

    //Parse a section tag and return html code
    private String parseSectionTag(final XmlResourceParser xml) throws XmlPullParserException, IOException {
        StringBuilder thanksBuilder = new StringBuilder();
        int eventType = xml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (xml.getName().equals(SectionTag.FUNCTION))) {
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(SectionTag.FUNCTION))){
                thanksBuilder.append(parseFunctionTag(xml));
            }
            eventType = xml.next();
        }
        return thanksBuilder.toString();
    }
    //Parse a function tag and return html code
    private String parseFunctionTag(final XmlResourceParser xml) throws XmlPullParserException, IOException {
        StringBuilder thanksBuilder = new StringBuilder("<span>" + xml.getAttributeValue(null, SectionTag.ATTRIBUTE_TITLE) + " " + "<b><i>" +  xml.getAttributeValue(null, SectionTag.ATTRIBUTE_AUTHOR) + "</b></i></span><ul>");
        int eventType = xml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (xml.getName().equals(SectionTag.CREDIT))) {
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(SectionTag.CREDIT))){
                eventType = xml.next();
                thanksBuilder.append("<li>").append(parseCreditTag(xml)).append("</li>");
            }
            eventType = xml.next();
        }
        thanksBuilder.append("</ul>");
        return thanksBuilder.toString();
    }

    //Parse the copyright tag and return html code
    private String parseCreditTag(final XmlResourceParser xml) throws XmlPullParserException, IOException {
        StringBuilder thanksBuilder = new StringBuilder();
        int eventType = xml.getEventType();
        while (!((eventType == XmlPullParser.END_TAG) && (xml.getName().equals(SectionTag.CREDIT)))) {
            if (eventType == XmlPullParser.TEXT){
                thanksBuilder.append(xml.getText());
            }
            eventType = xml.next();
        }
        return thanksBuilder.toString();
    }

    @SuppressLint("NewApi")
    //Get the credits in html code, this will be shown in the dialog's webview
    private String getHTMLTranks(final int resID, final Resources resources) {
        final StringBuilder thanksBuilder = new StringBuilder();
        thanksBuilder.append("<html><head>").append(getStyle()).append("</head><body>");
        try ( XmlResourceParser xml = resources.getXml(resID)) {
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(SectionTag.NAME))){
                    thanksBuilder.append(parseSectionTag(xml));
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
        thanksBuilder.append("</body></html>");
        return thanksBuilder.toString();
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
        final int resID = resources.getIdentifier(TRANKS_XML, "xml", packageName);
        //Create html credits
        final String htmlTranks = getHTMLTranks(resID, resources);

        //Get button strings
        final String close = resources.getString(R.string.close);

        //Check for empty credits
        if (htmlTranks.equals("")) {
            //Could not load credits, message user and exit
            Toast.makeText(mContext, "Could not load" + " "+ TRANKS_XML, Toast.LENGTH_SHORT).show();
            return;
        }
        //Create webview and load html
        final WebView WebView = new WebView(mContext);
        WebView.loadDataWithBaseURL(null, htmlTranks, "text/html", "utf-8", null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, StyleDialogColor)
                .setTitle(mTitle)
                .setView(WebView)
                .setPositiveButton(close, new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
}