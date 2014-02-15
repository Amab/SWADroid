/**
 * Information module for get courses's information
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.information;

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

import org.ksoap2.serialization.SoapObject;


public class Information extends Module {
	
	/**
     * Information Type. String with the type of information (none, HTML, plain text...)
     */
    private String infoSrc;
    
    /**
     * Information Content. String with the content of information.
     */
    private String infoTxt;
    
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
        
        
        case Constants.INTRODUCTION_REQUEST_CODE:
        
         	infoTypeToAdd = "introduction";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.introductionModuleLabel);
        
        break;

        case Constants.FAQS_REQUEST_CODE:
        
        	infoTypeToAdd = "";/*falta en la funcion que obtiene la informacion de las asignaturas*/
            
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.faqsModuleLabel);
        
        break;
        
        case Constants.BIBLIOGRAPHY_REQUEST_CODE:
        
        	infoTypeToAdd = "bibliography";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.bibliographyModuleLabel);
        
        break;
        
        case Constants.PRACTICESPROGRAM_REQUEST_CODE:
        
    		infoTypeToAdd = "practicals";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.practicesprogramModuleLabel);
        
        break;
        
        case Constants.THEORYPROGRAM_REQUEST_CODE:
        
    		infoTypeToAdd = "";/*falta en la funcion que obtiene la informacion de las asignaturas*/
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.theoryprogramModuleLabel);
            
        break;
        
        case  Constants.LINKS_REQUEST_CODE:
        
    		infoTypeToAdd = "links";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.linksModuleLabel);
        
        break;
        
        case Constants.TEACHINGGUIDE_REQUEST_CODE:
        
    		infoTypeToAdd = "guide";
        
            moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
            moduleIcon.setBackgroundResource(R.drawable.notif);

            moduleText = (TextView) this.findViewById(R.id.moduleName);
            moduleText.setText(R.string.teachingguideModuleLabel);
        
        break;
        
}

    }
    
    
	@Override
	protected void requestService() throws Exception {
		// TODO Auto-generated method stub
		
		createRequest(SOAPClient.CLIENT_TYPE);
		addParam("wsKey", Constants.getLoggedUser().getWsKey());
		addParam("courseCode", Constants.getSelectedCourseCode());
		addParam("infoType", infoTypeToAdd);
		sendRequest(User.class, false);
		
		 if (result != null) {
			 
			 //ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);	
			 SoapObject soap = (SoapObject) result;
			 //infoType = res.get(1).toString();
			 //infoContent = res.get(2).toString();
			 infoSrc = soap.getProperty(infoSrc).toString();
			 infoTxt = soap.getPrimitiveProperty(infoTxt).toString();
			 
		 }
		 
		 else{
			 
			 
			 
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