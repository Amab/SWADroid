/**
 * Information module for get courses's information
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.information;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.Module;


public class Information extends Module {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		
		
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_information_screen_layout);
        WebView webview = (WebView) this.findViewById(R.id.contentWebView);
             
        ImageView moduleIcon;
        TextView moduleText;
        
        this.findViewById(R.id.courseSelectedText).setVisibility(View.VISIBLE);
        
        int requestCode = this.getIntent().getIntExtra("requestCode", 0);
        
        TextView courseNameText = (TextView) this.findViewById(R.id.courseSelectedText);
        courseNameText.setText(Constants.getSelectedCourseShortName());

        switch (requestCode) {
        
        
        case Constants.INTRODUCTION_REQUEST_CODE : requestCode = 28;
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.introductionModuleLabel);
        
        break;

        case Constants.FAQS_REQUEST_CODE : requestCode = 29;
            
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.faqsModuleLabel);
        
        break;
        
        case Constants.BIBLIOGRAPHY_REQUEST_CODE : requestCode = 30;
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.bibliographyModuleLabel);
        
        break;
        
        case Constants.PRACTICESPROGRAM_REQUEST_CODE : requestCode = 31;
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.practicesprogramModuleLabel);
        
        break;
        
        case Constants.THEORYPROGRAM_REQUEST_CODE : requestCode = 32;
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.theoryprogramModuleLabel);
            
        break;
        
        case Constants.LINKS_REQUEST_CODE : requestCode = 33;
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.linksModuleLabel);
        
        break;
        
        case Constants.TEACHINGGUIDE_REQUEST_CODE : requestCode = 34;
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.teachingguideModuleLabel);
        
        break;
        
}

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