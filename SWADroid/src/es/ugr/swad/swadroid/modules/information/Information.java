/**
 * Information module for get courses's information
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.information;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.webservices.SOAPClient;


public class Information extends Module {
	
	/**
     * Information Type
     */
    private String infoType;
    
    /**
     * Information Content
     */
    private String infoContent;
    
    /**
     * Request code to get course's information
     */
    private String infoTypeToAdd;

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
        
         	infoTypeToAdd = "introduction";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.introductionModuleLabel);
        
        break;

        case Constants.FAQS_REQUEST_CODE : requestCode = 29;
        
        	infoTypeToAdd = "";/*falta en la funcion que obtiene la informacion de las asignaturas*/
            
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.faqsModuleLabel);
        
        break;
        
        case Constants.BIBLIOGRAPHY_REQUEST_CODE : requestCode = 30;
        
        	infoTypeToAdd = "bibliography";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.bibliographyModuleLabel);
        
        break;
        
        case Constants.PRACTICESPROGRAM_REQUEST_CODE : requestCode = 31;
        
    		infoTypeToAdd = "practicals";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.practicesprogramModuleLabel);
        
        break;
        
        case Constants.THEORYPROGRAM_REQUEST_CODE : requestCode = 32;
        
    		infoTypeToAdd = "";/*falta en la funcion que obtiene la informacion de las asignaturas*/
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.theoryprogramModuleLabel);
            
        break;
        
        case Constants.LINKS_REQUEST_CODE : requestCode = 33;
        
    		infoTypeToAdd = "links";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.linksModuleLabel);
        
        break;
        
        case Constants.TEACHINGGUIDE_REQUEST_CODE : requestCode = 34;
        
    		infoTypeToAdd = "guide";
        
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
		
		createRequest(SOAPClient.CLIENT_TYPE);
		addParam("wsKey", Constants.getLoggedUser().getWsKey());
		addParam("courseCode", Constants.getSelectedCourseCode());
		addParam("infoType", infoTypeToAdd);
		sendRequest(User.class, false);
		
		 if (result != null) {
			 
			 ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);		 
			 infoType = res.get(1).toString();
			 infoContent = res.get(2).toString();
			 
		 }
		
		
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