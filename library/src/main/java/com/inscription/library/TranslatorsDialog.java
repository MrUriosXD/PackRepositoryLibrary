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

public class TranslatorsDialog {
/*
 * Class to show a credits dialog
 *
 * Example xml
	<translators>
	    <section>
		    <function title="It has been translated by" author="">
		        <translator lang="">Hameno</translator>
		        <translator lang="">Pepyakin</translator>
			</function>
		</section>
	</translators>
 */
    static final private String TAG = "TranslatorsDialog";

    private static final String TRANSLATORS_XML = "translators";

    private int mTitle = R.string.title_translators;

    private final Context mContext;
    public TranslatorsDialog(final Context context) {
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
        String TRANSLATOR = "translator";
        String ATTRIBUTE_TITLE = "title";
        String ATTRIBUTE_AUTHOR = "author";
    }

    //Parse a section tag and return html code
    private String parseSectionTag(final XmlResourceParser xml) throws XmlPullParserException, IOException {
        StringBuilder translatorsBuilder = new StringBuilder();
        int eventType = xml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (xml.getName().equals(SectionTag.FUNCTION))) {
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(SectionTag.FUNCTION))){
                translatorsBuilder.append(parseFunctionTag(xml));
            }
            eventType = xml.next();
        }
        return translatorsBuilder.toString();
    }
    //Parse a function tag and return html code
    private String parseFunctionTag(final XmlResourceParser xml) throws XmlPullParserException, IOException {
        StringBuilder translatorsBuilder = new StringBuilder("<span>" + xml.getAttributeValue(null, SectionTag.ATTRIBUTE_TITLE) + " " + "<b><i>" +  xml.getAttributeValue(null, SectionTag.ATTRIBUTE_AUTHOR) + "</b></i></span><ul>");
        int eventType = xml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (xml.getName().equals(SectionTag.TRANSLATOR))) {
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(SectionTag.TRANSLATOR))){
                eventType = xml.next();
                translatorsBuilder.append("<li>").append(parseCreditTag(xml)).append("</li>");
            }
            eventType = xml.next();
        }
        translatorsBuilder.append("</ul>");
        return translatorsBuilder.toString();
    }

    //Parse the copyright tag and return html code
    private String parseCreditTag(final XmlResourceParser xml) throws XmlPullParserException, IOException {
        StringBuilder translatorsBuilder = new StringBuilder();
        int eventType = xml.getEventType();
        while (!((eventType == XmlPullParser.END_TAG) && (xml.getName().equals(SectionTag.TRANSLATOR)))) {
            if (eventType == XmlPullParser.TEXT){
                translatorsBuilder.append(xml.getText());
            }
            eventType = xml.next();
        }
        return translatorsBuilder.toString();
    }

    @SuppressLint("NewApi")
    //Get the credits in html code, this will be shown in the dialog's webview
    private String getHTMLTranslators(final int resID, final Resources resources) {
        final StringBuilder translatorsBuilder = new StringBuilder();
        translatorsBuilder.append("<html><head>").append(getStyle()).append("</head><body>");
        try ( XmlResourceParser xml = resources.getXml(resID)) {
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals(SectionTag.NAME))){
                    translatorsBuilder.append(parseSectionTag(xml));
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
        translatorsBuilder.append("</body></html>");
        return translatorsBuilder.toString();
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
        final int resID = resources.getIdentifier(TRANSLATORS_XML, "xml", packageName);
        //Create html credits
        final String HTML = getHTMLTranslators(resID, resources);

        //Get button strings
        final String close =  resources.getString(R.string.close);

        //Check for empty credits
        if (HTML.equals("")) {
            //Could not load credits, message user and exit
            Toast.makeText(mContext, "Could not load" + " "+ TRANSLATORS_XML, Toast.LENGTH_SHORT).show();
            return;
        }
        //Create webview and load html
        final WebView webView = new WebView(mContext);
        webView.loadData(HTML, "text/html", "utf-8");

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, StyleDialogColor)
                .setTitle(mTitle)
                .setView(webView)
                .setPositiveButton(close, new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
}