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

package es.ugr.swad.swadroid.modules.rollcall.students;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;

/**
 * Students array adapter.
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class StudentsArrayAdapter extends ArrayAdapter<StudentItemModel> {
	private final List<StudentItemModel> list;
	private final Activity context;
	private int widthScale, heightScale, bMapScaledWidth, bMapScaledHeight;
	private int callerId;

	public static final String TAG = Constants.APP_TAG + " InteractiveArrayAdapter";

	public StudentsArrayAdapter(Activity context, List<StudentItemModel> list, int callerId) {
		super(context, R.layout.list_items, list);
		this.context = context;
		this.list = list;
		this.callerId = callerId;
	}

	static class ViewHolder {
		protected ImageView image;
		protected TextView text;
		protected CheckBox checkbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		Bitmap bMap;

		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.list_image_items, null);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) view.findViewById(R.id.imageView1);
			viewHolder.text = (TextView) view.findViewById(R.id.TextView1);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
					StudentItemModel element = (StudentItemModel) viewHolder.checkbox.getTag();
					element.setSelected(buttonView.isChecked());
				}
			});

			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag();

		String photoFileName = list.get(position).getPhotoFileName();
		// If user has no photo, show default photo
		if (photoFileName == null) {
			bMap = BitmapFactory.decodeStream(viewHolder.image.getResources().openRawResource(R.raw.usr_bl));
		} else {
			String photoPath = getContext().getExternalFilesDir(null) + "/" + photoFileName;
			File photoFile = new File(photoPath);
			if (photoFile.exists()) {
				bMap = BitmapFactory.decodeFile(photoPath);
			} else {
				// If photoFile does not exist (has been deleted), show default photo
				bMap = BitmapFactory.decodeStream(viewHolder.image.getResources().openRawResource(R.raw.usr_bl));
			}
		}

		// Calculate the dimensions of the image to display as a function of the resolution of the screen
		Display display = ((WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		widthScale = 1200;
		heightScale = 2000;
		bMapScaledWidth = (bMap.getWidth() * display.getWidth()) / widthScale;
		bMapScaledHeight = (bMap.getHeight() * display.getHeight()) / heightScale;

		Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, bMapScaledWidth, bMapScaledHeight, true);
		viewHolder.image.setImageBitmap(bMapScaled);
		viewHolder.text.setText(list.get(position).toString());
		viewHolder.checkbox.setChecked(list.get(position).isSelected());
		if (callerId == Constants.STUDENTS_LIST_REQUEST_CODE || callerId == Constants.STUDENTS_HISTORY_REQUEST_CODE)
			viewHolder.checkbox.setVisibility(View.GONE);

		return view;
	}
}
