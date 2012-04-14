package es.ugr.swad.swadroid.modules.attendance;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;

public class InteractiveArrayAdapter extends ArrayAdapter<ListItemModel> {

	private final List<ListItemModel> list;
	private final Activity context;

	public static final String TAG = Global.APP_TAG + " InteractiveArrayAdapter";

	public InteractiveArrayAdapter(Activity context, List<ListItemModel> list) {
		super(context, R.layout.list_items, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected ImageView image;
		protected TextView text;
		protected CheckBox checkbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

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
					ListItemModel element = (ListItemModel) viewHolder.checkbox.getTag();
					element.setSelected(buttonView.isChecked());
				}
			});

			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}

		ViewHolder holder = (ViewHolder) view.getTag();

		holder.image.setImageResource(list.get(position).getImageId());
		Bitmap bMap = BitmapFactory.decodeResource(holder.image.getResources(), list.get(position).getImageId());
		Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 60, 80, true);
		holder.image.setImageBitmap(bMapScaled);
		
		holder.text.setText(list.get(position).getName());
		holder.checkbox.setChecked(list.get(position).isSelected());

		return view;
	}
}
