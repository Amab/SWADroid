package es.ugr.swad.swadroid.gui;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import es.ugr.swad.swadroid.R;

public class DialogFactory {
    /**
     * Creates a Webview dialog with HTML content.
     */
    public static AlertDialog createWebViewDialog(Context context, String title, String url) {
    	LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_webview, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNeutralButton(R.string.close_dialog, null);

        alertDialogBuilder.setView(promptsView);

        WebView webview = (WebView) promptsView.findViewById(R.id.webview_dialog);
        webview.loadUrl(url);
        
        return alertDialogBuilder.create();
    }
    
    /**
     * Creates a Webview dialog with HTML content.
     */
    public static AlertDialog createWebViewDialog(Context context, int title, int contentResourceId) {
    	LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_webview, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(title));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNeutralButton(R.string.close_dialog, null);

        alertDialogBuilder.setView(promptsView);
        
        try {
	        Resources res = context.getResources();
	        InputStream in_s = res.openRawResource(contentResourceId);
	        
	        /*byte[] b = new byte[in_s.available()];
	        in_s.read(b);
	        String content = new String(b, "UTF-8");*/
	        
	        String content = IOUtils.toString(in_s);
	        IOUtils.closeQuietly(in_s);

	        WebView webview = (WebView) promptsView.findViewById(R.id.webview_dialog);
	        webview.loadData(content, "text/html", "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return alertDialogBuilder.create();
    }
}
