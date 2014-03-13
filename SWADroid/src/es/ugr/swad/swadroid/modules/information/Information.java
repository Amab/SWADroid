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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_information_screen_layout);

		ImageView moduleIcon;
		TextView moduleText;

		this.findViewById(R.id.courseSelectedText).setVisibility(View.VISIBLE);

		int requestCode = this.getIntent().getIntExtra("requestCode", 0);

		TextView courseNameText = (TextView) this
				.findViewById(R.id.courseSelectedText);
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

			infoTypeToAdd = "FAQ";

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

		case Constants.SYLLABUSPRACTICALS_REQUEST_CODE:

			infoTypeToAdd = "practicals";

			moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
			moduleIcon.setBackgroundResource(R.drawable.notif);

			moduleText = (TextView) this.findViewById(R.id.moduleName);
			moduleText.setText(R.string.syllabusPracticalsModuleLabel);

			break;

		case Constants.SYLLABUSLECTURES_REQUEST_CODE:

			infoTypeToAdd = "lectures";

			moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
			moduleIcon.setBackgroundResource(R.drawable.notif);

			moduleText = (TextView) this.findViewById(R.id.moduleName);
			moduleText.setText(R.string.syllabusLecturesModuleLabel);

			break;

		case Constants.LINKS_REQUEST_CODE:

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

			
		case Constants.ASSESSMENT_REQUEST_CODE:

			infoTypeToAdd = "assessment";

			moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
			moduleIcon.setBackgroundResource(R.drawable.notif);

			moduleText = (TextView) this.findViewById(R.id.moduleName);
			moduleText.setText(R.string.assessmentModuleLabel);

			break;
		}
		
		
		setMETHOD_NAME("getCourseInfo");
	}

    @Override
    protected void onStart() {
        super.onStart();
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
		addParam("wsKey", Constants.getLoggedUser().getWsKey());
		addParam("courseCode", Constants.getSelectedCourseCode());
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
		WebView webview = (WebView) this.findViewById(R.id.info_webview_dialog);

		if (infoSrc.equals("none")) {
			webview.loadDataWithBaseURL(null,(getString(R.string.emptyInformation)),"text/html","utf-8",null);
		} else if (infoSrc.equals("URL")) {
			webview.loadDataWithBaseURL(infoTxt,null,"text/html","utf-8",null);
		} else {
			webview.loadDataWithBaseURL(null,infoTxt,"text/html","utf-8",null);
		}

	}

	@Override
	protected void onError() {

	}
}