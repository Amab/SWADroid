/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.modules.attendance;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dataframework.DataFramework;
import com.google.zxing.client.android.swadroid.model.DataBaseHelper;
import com.google.zxing.client.android.swadroid.model.User;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Attendance module for roll call in class
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Attendance extends Module {
	private ListView lv;
	private AlertDialog mAlertDialog;
	private List<ListItemModel> listModel;
	private ArrayList<String> listaDnis = null;
	private long selectedCourseCode;
	/**
	 * Database Helper.
	 */
	protected static DataBaseHelper dbHelper;    
	/**
	 * Database Framework.
	 */
	protected static DataFramework db;  


	/**
	 * Array adapter for showing menu options
	 */
	private ArrayAdapter<String> adapter;
	/**
	 * Attendance tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " Attendance";

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ImageView image;
		TextView text;
		ListView list;
		String[] items = getResources().getStringArray(R.array.attendanceMenuItems);

		OnItemClickListener clickListener = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent activity;
				switch(position) {
				case 0:
					activity = new Intent(getBaseContext(), AttendanceConfigDownload.class);
					startActivityForResult(activity, Global.ATTENDANCE_CONFIG_DOWNLOAD_REQUEST_CODE);					
					break;
				case 1:

					break;
				}

			}    	
		};

		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_items);

		image = (ImageView) this.findViewById(R.id.moduleIcon);
		image.setBackgroundResource(R.drawable.attendance);

		text = (TextView) this.findViewById(R.id.moduleName);
		text.setText(R.string.attendanceModuleLabel);

		adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, R.id.listText, items);
		list = (ListView) this.findViewById(R.id.listItems);
		list.setAdapter(adapter);
		list.setOnItemClickListener(clickListener);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
		case Global.ATTENDANCE_CONFIG_DOWNLOAD_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				// Show a dialog with the list of ID cards scanned
				listaDnis = intent.getStringArrayListExtra("lista_dnis");

				selectedCourseCode = intent.getLongExtra("selectedCourseCode", 0);

				Log.i(TAG, "selectedCourseCode=" + selectedCourseCode);

				if (listaDnis == null)
					Toast.makeText(getApplicationContext(), "No se han detectado codigos validos", Toast.LENGTH_SHORT).show();
				else if (!listaDnis.isEmpty()) {
					listModel = new ArrayList<ListItemModel>();

					// Initialize database
					try {
						db = DataFramework.getInstance();
						db.open(this, "es.ugr.swad.swadroid");
						dbHelper = new DataBaseHelper(db);
					} catch (Exception ex) {
						Log.e(ex.getClass().getSimpleName(), ex.getMessage());
						ex.printStackTrace();
					}

					// utilizar aqui el dni para buscar el usuario, y si existe en el grupo seleccionado, ponerlo como marcado
					for (String dni: listaDnis) {
						User u = dbHelper.getUser(dni, selectedCourseCode);
						String userName;

						if (u != null) {
							userName = u.getUserFirstname() + " " + u.getUserSurname1() + " " + u.getUserSurname2();
							// We put the default photo for each item in the list
							listModel.add(new ListItemModel(userName, R.drawable.usr_bl));
						}
					}
					// Marcamos como asistentes a todos los escaneados (cambiar cuando pueda comprobarse el grupo)
					// Mark as attending all scans (change when the group can be checked)
					for (ListItemModel i: listModel)
						i.setSelected(true);

					ArrayAdapter<ListItemModel> modeAdapter = new InteractiveArrayAdapter(this, listModel);		
					lv = new ListView(this);
					lv.setAdapter(modeAdapter);

					lv.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							// When clicked, show a toast with the TextView text
							Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
						}
					});

					prepareAlertDialog();
					mAlertDialog.show();
				}
			}
			break;
		}

		/*String dni_escaneado = intent.getStringExtra("SCAN_RESULT");
				String formato = intent.getStringExtra("SCAN_RESULT_FORMAT");

				if (!formato.contentEquals("QR_CODE"))
					Toast.makeText(
							getApplicationContext(),
							"ERROR: el codigo detectado no es un codigo QR valido",
							Toast.LENGTH_SHORT).show();
				else if (!Util.isValidDni(dni_escaneado))
					Toast.makeText(
							getApplicationContext(),
							"ERROR: el codigo detectado no contiene un DNI valido",
							Toast.LENGTH_SHORT).show();
				else {
					Toast.makeText(getApplicationContext(), "DNI valido: " + dni_escaneado, Toast.LENGTH_SHORT).show();
					listaDnis.add(dni_escaneado);
				}
				break;*/
		/*			}
		} else {
		}*/
	}

	public void prepareAlertDialog() {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		AlertDialog.Builder mBuider = new AlertDialog.Builder(this);
		mBuider.setTitle("Estudiantes asistentes");
		mBuider.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		mBuider.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		mBuider.setView(lv);
		mAlertDialog = mBuider.create();

		lp.copyFrom(mAlertDialog.getWindow().getAttributes());
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws NoSuchAlgorithmException, IOException, XmlPullParserException, SoapFault, IllegalAccessException, InstantiationException {
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {
	}

	@Override
	protected void onError() {
	}

}
