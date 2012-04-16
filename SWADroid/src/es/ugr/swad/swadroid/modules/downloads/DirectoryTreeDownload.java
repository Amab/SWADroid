package es.ugr.swad.swadroid.modules.downloads;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.widget.Toast;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;

/**
 * DirectoryTreeDownload  gets directory tree of files of general documents of a course/group
 * or documents from shared area of a course/group
 * @author Helena Rodríguez Gijón <hrgijon@gmail.com>
 * */

public class DirectoryTreeDownload extends Module {
	private int treeCode; //documents of course or common zone of course
	private int group = 0; // documents of the course
	//TODO esta clase tiene q cambiar
	public class DirectoryTree{
		String tree;
	}

	@Override
	protected void requestService() throws NoSuchAlgorithmException,
	IOException, XmlPullParserException, SoapFault,
	IllegalAccessException, InstantiationException {
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		addParam("courseCode", (int)Global.getSelectedCourseCode());
		addParam("groupCode", group);
		addParam("treeCode", treeCode);
		sendRequest(User.class,true);
		if(result!=null){
			SoapObject soapObject = (SoapObject) result;
			String tree = soapObject.getProperty("tree").toString();

			//directoryTree = new DirectoryTree();
			setResult(RESULT_OK);
		}
	}

	@Override
	protected void connect() {
		String progessDescription= getString(R.string.documentsDownloadProgressDescription);
		int progressTitle = R.string.documentsDownloadModuleLabel;
		Toast.makeText(this, getString(R.string.documentsDownloadProgressDescription), Toast.LENGTH_LONG).show();
		Connect con = new Connect(false,progessDescription,progressTitle,true);
		con.execute();

	}

	@Override
	protected void postConnect() {
		//Toast.makeText(this, "got directory tree", Toast.LENGTH_LONG).show();
		//Log.i(TAG, "directory tree");

		finish();

	}

	@Override
	protected void onError() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMETHOD_NAME("getDirectoryTree");
	}

	@Override
	protected void onStart() {
		super.onStart();
		treeCode = getIntent().getIntExtra("treeCode", Global.DOCUMENTS_AREA_CODE); 
		runConnection();
		if(!isConnected){
			setResult(RESULT_CANCELED);
			finish();
		}
	}

}
