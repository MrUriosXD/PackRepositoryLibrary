/*
 * (c) 2013 Martin van Zuilekom (http://martin.cubeactive.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.inscription.library;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import static com.inscription.library.util.AppUtils.StyleDialogColor;

/*
 * Class to show a credits dialog
 *
 * Example xml
	<credits>
	    <section title="Development">
		    <function title="Created by">
		        <credit>Martin van Zuilekom</credit>        
			</function>	    	    	   
		    <function title="Code contribution">
		        <credit>Hameno</credit>        
		        <credit>Pepyakin</credit>        
			</function>	    	    	   
		</section>
		<websites>
		    <url title="Inscription on Github">https://github.com/MartinvanZ/Inscription</url>
		</websites>	
		<copyright>(c) 2013 Martin van Zuilekom. Licensed under the Apache License, Version 2.0 (the "License").<url>http://www.apache.org/licenses/LICENSE-2.0</url></copyright>
	</credits> 
 */

public class CreditsDialog {
    static final private String TAG = "CreditsDialog";

    static final private String CREDITS_XML = "credits";

	private int mTitle = R.string.title_credits;

	private String mStyle =   "body { font-size: 9pt; text-align: center; }"
			+ "h1 { margin-top: 20px; margin-bottom: 15px; margin-left: 0px; font-size: 1.7EM; text-align: center; }"
			+ "h2 { margin-top: 15px; margin-bottom: 5px; padding-left: 0px; margin-left: 0px; font-size: 1EM; }"
			+ "li { margin-left: 0px; font-size: 1EM; }"
			+ "ul { margin-top: 0px;   margin-bottom: 5px; padding-left: 0px; list-style-type: none; }"
			+ "a { color: #777777; }"				
			+ "</style>";

    private final int mIcon = 0;

	private final Context mContext;

	public CreditsDialog(final Context context) {
        mContext = context;
    }

	//CSS style for the html
	private String getStyle() {
		return String.format("<style type=\"text/css\">%s</style>", mStyle);
	}

	public void setStyle(final String style) {
		mStyle = style;
	}

	// and we can set a custom Title, default is "credits"
	private int getCustomTitle() {
		return mTitle ;
	}

	public void setCustomTitle(final int title) {
		mTitle = title;
	}

	//Parse the copyright tag and return html code
	private String parseCreditTag(final XmlResourceParser aXml) throws XmlPullParserException, IOException {
		StringBuilder creditsBuilder = new StringBuilder();
        int eventType = aXml.getEventType();
        while (!((eventType == XmlPullParser.END_TAG) && (aXml.getName().equals("credit")))) {
        	if (eventType == XmlPullParser.TEXT){
        		creditsBuilder.append(aXml.getText());
            }
        	if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("url"))){
        		creditsBuilder.append(parseUrlTag(aXml));
            }
        	eventType = aXml.next();
        }		
        return creditsBuilder.toString();
	}
	
	//Parse a function tag and return html code
	private String parseFunctionTag(final XmlResourceParser aXml) throws XmlPullParserException, IOException {
		StringBuilder creditsBuilder = new StringBuilder("<ul>");
        int eventType = aXml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (aXml.getName().equals("credit"))) {
        	if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("credit"))){
            	eventType = aXml.next();
        		creditsBuilder.append("<li>").append(parseCreditTag(aXml)).append("</li>");
            }
        	eventType = aXml.next();
        }		
        creditsBuilder.append("</ul>");
        return creditsBuilder.toString();
	}
	
	//Parse a section tag and return html code
	private String parseSectionTag(final XmlResourceParser aXml) throws XmlPullParserException, IOException {
		StringBuilder creditsBuilder = new StringBuilder("<h1>" + aXml.getAttributeValue(null, "title") + "</h1>");
        int eventType = aXml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (aXml.getName().equals("function"))) {
        	if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("function"))){
        		creditsBuilder.append(parseFunctionTag(aXml));
            }
        	eventType = aXml.next();
        }		
        return creditsBuilder.toString();
	}

	//Parse the url tag and return html code
	private String parseUrlTag(final XmlResourceParser aXml) throws XmlPullParserException, IOException {
        int eventType = aXml.getEventType();
		String url = "";
		String title = "";
		if (aXml.getAttributeValue(null, "title") != null)
			title = aXml.getAttributeValue(null, "title");
        while (eventType != XmlPullParser.END_TAG) {
        	if (eventType == XmlPullParser.TEXT){
        		url = aXml.getText();
            }
        	eventType = aXml.next();
        }		
		if (url.equals(""))
			return "";
		if (title.equals(""))
			title = url;
		return String.format("<br /><a href ='%1$s'>%2$s</a>", url, title);
	}
	
	//Parse the copyright tag and return html code
	private String parseCopyrightTag(final XmlResourceParser aXml) throws XmlPullParserException, IOException {
		StringBuilder creditsBuilder = new StringBuilder("<br /><br />");
        int eventType = aXml.getEventType();
        while (!((eventType == XmlPullParser.END_TAG) && (aXml.getName().equals("copyright")))) {
        	if (eventType == XmlPullParser.TEXT){
        		creditsBuilder.append(aXml.getText());
            }
        	if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("url"))){
        		creditsBuilder.append(parseUrlTag(aXml));
            }
        	eventType = aXml.next();
        }		
        return creditsBuilder.toString();
	}
	
	//Parse the websites tag and return html code
	private String parseWebsitesTag(final XmlResourceParser aXml) throws XmlPullParserException, IOException {
		StringBuilder creditsBuilder = new StringBuilder();
        int eventType = aXml.getEventType();
        while (!((eventType == XmlPullParser.END_TAG) && (aXml.getName().equals("websites")))) {
        	if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("url"))){
        		creditsBuilder.append(parseUrlTag(aXml));
            }
        	eventType = aXml.next();
        }		
        return creditsBuilder.toString();
	}

	//Get the credits in html code, this will be shown in the dialog's webview
	private String getHTMLCredits(final int aResourceId, final Resources aResource) {
		StringBuilder creditsBuilder = new StringBuilder("<html><head>" + getStyle() + "</head><body>");
		try (XmlResourceParser xml = aResource.getXml(aResourceId)) {
			int eventType = xml.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("section"))) {
					creditsBuilder.append(parseSectionTag(xml));

				}
				if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("copyright"))) {
					creditsBuilder.append(parseCopyrightTag(xml));

				}
				if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("websites"))) {
					creditsBuilder.append(parseWebsitesTag(xml));

				}
				eventType = xml.next();
			}
		} catch (final XmlPullParserException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (final IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		creditsBuilder.append("</body></html>");
		return creditsBuilder.toString();
	}
	
	//Call to show the credits dialog
    public void show() {
    	//Get resources
    	final String packageName = mContext.getPackageName();
    	Resources resources;
		try {
			resources = mContext.getPackageManager().getResourcesForApplication(packageName);
		} catch (final NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
		String title = resources.getString(R.string.title_credits);

        //Get credits xml resource id
      	final int resID = resources.getIdentifier(CREDITS_XML, "xml", packageName);
        //Create html credits
       	final String htmlCredits = getHTMLCredits(resID, resources);

        //Get button strings
        final String Close =  resources.getString(R.string.close);

        //Check for empty credits
        if (htmlCredits.equals("")) {
        	//Could not load credits, message user and exit
        	Toast.makeText(mContext, "Could not load" + " " + resources.getString(R.string.title_credits), Toast.LENGTH_SHORT).show();
        	return;
        }
        //Create webview and load html
		final WebView WebView = new WebView(mContext);
		WebView.loadDataWithBaseURL(null, htmlCredits, "text/html", "utf-8", null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, StyleDialogColor)
                .setTitle(getCustomTitle())
                .setView(WebView)
                .setPositiveButton(Close, new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

}
