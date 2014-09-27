/**
 * Information module for get courses's information
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.information;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.SWADroidTracker;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.modules.Login;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.webservices.SOAPClient;

public class Information extends Module {

	public static final String TAG = Constants.APP_TAG + " Information";

	/**
	 * Information Type. String with the type of information (none, HTML, plain
	 * text...)
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

	/**
	 * Webview to show course's information
	 */
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_information_screen_layout);
		
		webview = (WebView) this.findViewById(R.id.info_webview_dialog);
		
		WebSettings settings = webview.getSettings();
		settings.setDefaultTextEncodingName("utf-8");

		int requestCode = this.getIntent().getIntExtra("requestCode", 0);

		getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());

		switch (requestCode) {

		case Constants.INTRODUCTION_REQUEST_CODE:

			infoTypeToAdd = "introduction";
			
			setTitle(R.string.introductionModuleLabel);
			getSupportActionBar().setIcon(R.drawable.info);

			break;
			
		case Constants.TEACHINGGUIDE_REQUEST_CODE:

			infoTypeToAdd = "guide";
			
			setTitle(R.string.teachingguideModuleLabel);
			getSupportActionBar().setIcon(R.drawable.file);

			break;
			
		case Constants.SYLLABUSLECTURES_REQUEST_CODE:

			infoTypeToAdd = "lectures";
			
			setTitle(R.string.syllabusLecturesModuleLabel);
			getSupportActionBar().setIcon(R.drawable.syllabus);

			break;
			
		case Constants.SYLLABUSPRACTICALS_REQUEST_CODE:

			infoTypeToAdd = "practicals";
			
			setTitle(R.string.syllabusPracticalsModuleLabel);
			getSupportActionBar().setIcon(R.drawable.lab);

			break;
			
		case Constants.BIBLIOGRAPHY_REQUEST_CODE:

			infoTypeToAdd = "bibliography";
			
			setTitle(R.string.bibliographyModuleLabel);
			getSupportActionBar().setIcon(R.drawable.book);

			break;

		case Constants.FAQS_REQUEST_CODE:

			infoTypeToAdd = "FAQ";
			
			setTitle(R.string.faqsModuleLabel);
			getSupportActionBar().setIcon(R.drawable.faq);

			break;


		case Constants.LINKS_REQUEST_CODE:

			infoTypeToAdd = "links";
			
			setTitle(R.string.linksModuleLabel);
			getSupportActionBar().setIcon(R.drawable.link);

			break;

			
		case Constants.ASSESSMENT_REQUEST_CODE:

			infoTypeToAdd = "assessment";
			
			setTitle(R.string.assessmentModuleLabel);
			getSupportActionBar().setIcon(R.drawable.info);

			break;
		}		
		
		setMETHOD_NAME("getCourseInfo");
	}

    @Override
    protected void onStart() {
        super.onStart();

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " " + infoTypeToAdd);
        try {
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(TAG, errorMsg, e, true);
        }
    }

	@Override
	protected void connect() {
		String progressDescription = getString(R.string.informationProgressDescription);
		int progressTitle = R.string.informationProgressTitle;

		startConnection(true, progressDescription, progressTitle);
	}

	@Override
	protected void requestService() throws Exception {
		createRequest(SOAPClient.CLIENT_TYPE);
		addParam("wsKey", Login.getLoggedUser().getWsKey());
		addParam("courseCode", Courses.getSelectedCourseCode());
		addParam("infoType", infoTypeToAdd);
		sendRequest(User.class, true);

		if (result != null) {
			SoapObject soap = (SoapObject) result;
			infoSrc = soap.getProperty("infoSrc").toString();
			infoTxt = soap.getPrimitiveProperty("infoTxt").toString();

			// Request finalized without errors
			setResult(RESULT_OK);
		} else {
			infoTxt = getString(R.string.connectionRequired);
		}
	}

	@Override
	protected void postConnect() {
		if (infoSrc.equals("none") || infoTxt.equals("") || infoTxt.equals(Constants.NULL_VALUE)) {
			webview.loadDataWithBaseURL(null,(getString(R.string.emptyInformation)), "text/html", "utf-8", null);
		} else if (infoSrc.equals("URL")) {
			webview.loadUrl(infoTxt);
		} else {
			webview.loadDataWithBaseURL(null, infoTxt, "text/html", "utf-8", null);
		}
	}

	@Override
	protected void onError() {
		webview.loadDataWithBaseURL(null, getString(R.string.emptyInformation), "text/html", "utf-8", null);
	}
}