package es.ugr.swad.swadroid.gui;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.bugsense.trace.BugSenseHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
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
    
    public static AlertDialog neutralDialog(Context context, int title, int message) {
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(R.string.close_dialog, null);
    	
    	return alertDialogBuilder.create();
    }
    
    public static AlertDialog positiveNegativeDialog(Context context, int layoutId, int titleId, int messageId,
    		int acceptLabel, int cancelLabel,
    		DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener,
    		DialogInterface.OnCancelListener cancelListener) {
    	
    	AlertDialog alertDialog;
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
        .setTitle(titleId)
        .setCancelable(false)
        .setPositiveButton(acceptLabel, positiveListener)
        .setNegativeButton(cancelLabel, negativeListener);
    	
    	if(messageId != -1) {
    		alertDialogBuilder.setMessage(messageId);
    	}
    	
    	if(cancelListener != null) {
    		alertDialogBuilder.setCancelable(true);
    		alertDialogBuilder.setOnCancelListener(cancelListener);
    	}
    	
    	if(layoutId != -1) {    		
    		FrameLayout frameView = new FrameLayout(context);
    		alertDialogBuilder.setView(frameView);

    		alertDialog = alertDialogBuilder.create();
    		LayoutInflater inflater = alertDialog.getLayoutInflater();
    		inflater.inflate(layoutId, frameView);
    	} else {
    		alertDialog = alertDialogBuilder.create();
    	}
    	
    	return alertDialog;
    }
    
    public static AlertDialog errorDialog(Context context, String tag, String message, Exception ex,
    		boolean sendException, boolean isDebuggable, DialogInterface.OnClickListener onClickListener) {
    	
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(R.string.title_error_dialog)
                .setMessage(message)
                .setNeutralButton(R.string.close_dialog, onClickListener)
                .setIcon(R.drawable.erroricon);

        if (ex != null) {
            ex.printStackTrace();

            // Send exception details to Bugsense
            if (!isDebuggable && sendException) {
                BugSenseHandler.sendExceptionMessage(tag, message, ex);
            }
        }
        
        return alertDialogBuilder.create();
    }
}
