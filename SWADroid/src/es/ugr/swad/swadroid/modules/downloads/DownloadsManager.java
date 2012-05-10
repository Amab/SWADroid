/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Helena Rodriguez Gijon <hrgijon@gmail.com>
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
package es.ugr.swad.swadroid.modules.downloads;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.MenuActivity;
import es.ugr.swad.swadroid.R;


/**
 * Activity to navigate through the directory tree of documents and to manage the downloads of documents
 * @author Helena Rodríguez Gijon <hrgijon@gmail.com>
 * */
public class DownloadsManager extends MenuActivity {
	/**
	 * Class that contains the directory tree and gives information of each level
	 * */
	private DirectoryNavigator	navigator;
	
	/**
	 * Specifies whether to display the documents or the shared area of the subject
	 * 1 specifies documents area
	 * 2 specifies shared area
	 * */
	private int downloadsCode = 0;
	/**
	 * String that contains the xml files recevied from the web service 
	 * */
	private String tree;
	
    /**
     * Downloads tag name for Logcat
     */
    public static final String TAG = Global.APP_TAG + " Downloads";

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Intent activity;
		activity = new Intent(getBaseContext(),DirectoryTreeDownload.class);
		activity.putExtra("treeCode",downloadsCode);
		startActivityForResult(activity,Global.DIRECTORY_TREE_REQUEST_CODE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		downloadsCode = getIntent().getIntExtra("downloadsCode", Global.DOCUMENTS_AREA_CODE);

		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch(requestCode){
			//After get the list of courses, a dialog is launched to choice the course
			case Global.DIRECTORY_TREE_REQUEST_CODE:
				tree = data.getStringExtra("tree");
				setMainView();
				break;
			}
		}
	}
	
	private void setMainView(){
		ImageView image;
		TextView text;
		setContentView(R.layout.navigation);
		
		if(downloadsCode == Global.DOCUMENTS_AREA_CODE){
	        image = (ImageView)this.findViewById(R.id.moduleIcon);
	        image.setBackgroundResource(R.drawable.folder);
	        
	        text = (TextView)this.findViewById(R.id.moduleName);
	        text.setText(R.string.documentsDownloadModuleLabel);		
		}else{ //SHARE_AREA_CODE
	        image = (ImageView)this.findViewById(R.id.moduleIcon);
	        image.setBackgroundResource(R.drawable.folderusers);
	        
	        text = (TextView)this.findViewById(R.id.moduleName);
	        text.setText(R.string.sharedsDownloadModuleLabel);				
		}
		
		TextView text2;
		text2= (TextView) this.findViewById(R.id.path);
		text2.setText(R.string.blogTitle);
		text2.setText("Asignatura de Prueba /Tema 1/Sobre SWAD");
		navigator = new DirectoryNavigator(tree);
		GridView grid = (GridView) this.findViewById(R.id.gridview);
		/*ArrayList<DirectoryItem> n = new ArrayList<DirectoryItem>();
		DirectoryItem tema1 = new DirectoryItem("Tema 1","dir","url",2,21234);
		DirectoryItem SobreSwad = new DirectoryItem("Sobre SWAD","dir","url",2,21234);
		DirectoryItem LogoS = new DirectoryItem("Logo de SWAD","file","url",2,21234);
		DirectoryItem LogoP = new DirectoryItem("Logo de Prado","dir","url",2,21234);
		n.add(tema1);
		n.add(SobreSwad);
		n.add(LogoS);
		n.add(LogoP);*/
		ArrayList<DirectoryItem> r = (ArrayList<DirectoryItem>) navigator.goToRoot();
		grid.setAdapter(new NodeAdapter(this,r));
		//grid.setAdapter(new ImageAdapter(this));
	}
	/*public class ImageAdapter extends BaseAdapter{
		Context mContext;
		public static final int ACTIVITY_CREATE = 10;
		public ImageAdapter(Context c){
			mContext = c;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v;
			if(convertView==null){
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.grid_item , null);
				TextView tv = (TextView)v.findViewById(R.id.icon_text);
				//if(position ==0)tv.setText("Tema 1");
				//if(position == 0)tv.setText("Sobre SWAD");
				if(position == 0)tv.setText("Logo de SWAD");
				if(position == 1)tv.setText("Logo de PRADO");
				if(position == 2)tv.setText("SWAD_mejoras futuras");
				
				ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
				//if(position == 0)iv.setImageResource(R.drawable.folder);
				if(position==1 || position ==0) iv.setImageResource(R.drawable.file);
				if(position==2) iv.setImageResource(R.drawable.imgres);
			}
			else
			{
				v = convertView;
			}
			return v;
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}*/

}
