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
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.Module;


public class Introduction extends Module {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		
		
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_information_screen_layout);
        WebView webview = (WebView) this.findViewById(R.id.contentWebView);
        
        ImageView moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
        moduleIcon.setBackgroundResource(R.drawable.notif);

        TextView moduleText = (TextView) this.findViewById(R.id.moduleName);
        moduleText.setText(R.string.introductionModuleLabel);
       
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