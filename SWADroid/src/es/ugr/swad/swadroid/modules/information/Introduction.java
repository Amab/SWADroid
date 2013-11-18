/**
 * introduction module for get courses's introduction
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.information;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.webkit.WebView;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.Module;


public class Introduction extends Module {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		
		WebView webview;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_information_screen_layout);
        webview = (WebView) this.findViewById(R.id.contentWebView);
       
    }
    
    
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
			IOException, XmlPullParserException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void connect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postConnect() {
		// TODO Auto-generated method stub
		finish();
		
	}

	@Override
	protected void onError() {
		// TODO Auto-generated method stub
		
	}

}