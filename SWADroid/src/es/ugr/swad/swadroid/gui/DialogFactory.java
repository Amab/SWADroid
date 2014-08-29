package es.ugr.swad.swadroid.gui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.bugsense.trace.BugSenseHandler;
import es.ugr.swad.swadroid.R;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
/**
 * Class for create dialogs.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class DialogFactory {
	/**
	 * Creates a Webview dialog with HTML content.
	 * @param context Application context
	 * @param title Dialog title as string
	 * @param url URL to be loaded
	 * @return AlertDialog with the HTML content loaded
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
     * @param context Application context
     * @param titleId Resource id of dialog title string
     * @param contentResourceId Resource id of HTML to be loaded
     * @return AlertDialog with the HTML content loaded
     */
    public static AlertDialog createWebViewDialog(Context context, int titleId, int contentResourceId) {
    	LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_webview, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(titleId));
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
    
    /**
     * Creates an AlertDialog with a neutral button
     * @param context Application context
     * @param layoutId Resource id of dialog layout
     * @param titleId Resource id of dialog title string
     * @param messageId Resource id of dialog message string
     * @param buttonLabelId Resource id of button label string
     * @param clickListener ClickListener associated to the neutral button
     * @return AlertDialog with a neutral button
     */
    public static AlertDialog createNeutralDialog(Context context, int layoutId, int titleId, int messageId,
    		int buttonLabelId, OnClickListener clickListener) {
    	
    	AlertDialog alertDialog;
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setCancelable(false)
                .setNeutralButton(buttonLabelId, clickListener);
    	
    	if(messageId != -1) {
    		alertDialogBuilder.setMessage(messageId);
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
    
    /**
     * Creates an AlertDialog with a positive button and a negative button
     * @param context Application context
     * @param iconId Resource id of dialog icon
     * @param layoutId Resource id of dialog layout
     * @param titleId Resource id of dialog title string
     * @param messageId Resource id of dialog message string
     * @param acceptLabel Resource id of positive button label string
     * @param cancelLabel Resource id of negative button label string
     * @param cancelable Indicates if dialog is cancelable
     * @param positiveListener ClickListener associated to the positive button
     * @param negativeListener ClickListener associated to the negative button
     * @param cancelListener ClickListener associated to the cancel dialog action
     * @return AlertDialog with a positive button and a negative button
     */
    public static AlertDialog createPositiveNegativeDialog(Context context, int iconId, int layoutId, int titleId,
    		int messageId, int acceptLabel, int cancelLabel, boolean cancelable, OnClickListener positiveListener,
    		OnClickListener negativeListener, OnCancelListener cancelListener) {
    	
    	AlertDialog alertDialog;
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
        .setTitle(titleId)
        .setCancelable(cancelable)
        .setPositiveButton(acceptLabel, positiveListener)
        .setNegativeButton(cancelLabel, negativeListener);
    	
    	if(iconId != -1) {
    		alertDialogBuilder.setIcon(iconId);
    	}
    	
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
    

    
    /**
     * Creates a warning dialog with a positive button and a negative button
     * @param context Application context
     * @param layoutId Resource id of dialog layout
     * @param titleId Resource id of dialog title string
     * @param messageId Resource id of dialog message string
     * @param acceptLabel Resource id of positive button label string
     * @param cancelLabel Resource id of negative button label string
     * @param cancelable Indicates if dialog is cancelable
     * @param positiveListener ClickListener associated to the positive button
     * @param negativeListener ClickListener associated to the negative button
     * @param cancelListener ClickListener associated to the cancel dialog action
     * @return AlertDialog with a positive button and a negative button
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static AlertDialog createWarningDialog(Context context, int layoutId, int titleId,
    		int messageId, int acceptLabel, int cancelLabel, boolean cancelable, OnClickListener positiveListener,
    		OnClickListener negativeListener, OnCancelListener cancelListener) {
    	
    	AlertDialog alertDialog;
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
        .setTitle(titleId)
        .setCancelable(cancelable)
        .setPositiveButton(acceptLabel, positiveListener)
        .setNegativeButton(cancelLabel, negativeListener);
    	
    	if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
    		alertDialogBuilder.setIconAttribute(android.R.attr.alertDialogIcon);
    	} else {
        	alertDialogBuilder.setIcon(R.drawable.ic_dialog_alert);    		
    	}
    	
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
    
    /**
     * Creates an error dialog and sends an error report
     * @param context Application context
     * @param tag Module tag
     * @param message Error message string
     * @param ex Exception thrown
     * @param sendException true if the error report has to be sended
     * 						false otherwise
     * @param isDebuggable	true if the application is debuggable (develop mode). Activates Logcat messages
     * 						false otherwise
     * @param onClickListener ClickListener associated to the neutral button
     * @return Error dialog and sends an error report
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static AlertDialog createErrorDialog(Context context, String tag, String message, Exception ex,
    		boolean sendException, boolean isDebuggable, DialogInterface.OnClickListener onClickListener) {
    	
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(R.string.title_error_dialog)
                .setMessage(message)
                .setNeutralButton(R.string.close_dialog, onClickListener);
    	    	
    	if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
    		alertDialogBuilder.setIconAttribute(android.R.attr.alertDialogIcon);
    	} else {
        	alertDialogBuilder.setIcon(R.drawable.ic_dialog_alert);    		
    	}

        if (ex != null) {
            //Log.e(tag, ex.getMessage());

            // Send exception details to Bugsense
            if (!isDebuggable && sendException) {
                BugSenseHandler.sendExceptionMessage(tag, message, ex);
            }
        }
        
        return alertDialogBuilder.create();
    }
}
