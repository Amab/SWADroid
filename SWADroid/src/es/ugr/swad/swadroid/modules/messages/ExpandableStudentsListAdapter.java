package es.ugr.swad.swadroid.modules.messages;

import java.io.File;
import java.util.ArrayList;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentItemModel;

/**
 * Custom ExpandableListAdapter for display users at Messages
 *
 * @author José Antonio Guerrero Avilés <cany20@gmail.com>
 */

public class ExpandableStudentsListAdapter extends BaseExpandableListAdapter{

	private ArrayList<String> groupItem;
	private ArrayList<List<Model>> childItem;
	private LayoutInflater minflater;
	private Activity activity;
	
	private int widthScale, heightScale, bMapScaledWidth, bMapScaledHeight;
	private final int callerId;
	
	
	public ExpandableStudentsListAdapter(ArrayList<String> grList,
			ArrayList<List<Model>> childItem2, int callerId) {
		groupItem = grList;
        this.childItem = childItem2;
        this.callerId = callerId;
	}

	public ExpandableStudentsListAdapter(Activity act, ArrayList<String> grList,
			ArrayList<List<Model>> childItem2, int callerId) {
		groupItem = grList;
		activity = act;
		minflater = LayoutInflater.from(activity);
        this.childItem = childItem2;
        this.callerId = callerId;
	}

	public void setInflater(LayoutInflater mInflater, Activity act) {
		this.minflater = mInflater;
		activity = act;
	}

	public void setActivity(Activity act) {
		activity = act;
		minflater = LayoutInflater.from(activity);
	}
	
	
	@Override
	public int getGroupCount() {
		return groupItem.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childItem.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupItem.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childItem.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
			
		TextView groupTitle;
		
		if (convertView == null) {
			convertView = minflater.inflate(R.layout.messages_group_item, parent, false);
		}
		
		groupTitle = (TextView) convertView.findViewById(R.id.messages_group_item_title);		
		groupTitle.setText(groupItem.get(groupPosition));
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
        Bitmap bMap;

        User u = (User) childItem.get(groupPosition).get(childPosition);
        
        final String fullName = u.getUserSurname1() 
        						+' '
        						+u.getUserSurname2()
        						+ ","
        						+ u.getUserFirstname();
        
        final String photoFileName = u.getPhotoFileName();
        
        if (convertView == null) {
        	convertView = minflater.inflate(R.layout.messages_child_item, null);
        }
        
        ImageView image = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView text = (TextView) convertView.findViewById(R.id.TextView1);
        final CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.check);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkbox.setSelected(buttonView.isChecked());
            }
        });


        // If user has no photo, show default photo
        if (photoFileName == null) {
            bMap = BitmapFactory.decodeStream(image.getResources().openRawResource(R.raw.usr_bl));
        } else {
            String photoPath = activity.getBaseContext().getExternalFilesDir(null) + "/" + photoFileName;
            File photoFile = new File(photoPath);
            //if (photoFile.exists()) {
                //bMap = BitmapFactory.decodeFile(photoPath);
            //} else {
                // If photoFile does not exist (has been deleted), show default photo
                bMap = BitmapFactory.decodeStream(image.getResources().openRawResource(R.raw.usr_bl));
            //}
        }

        // Calculate the dimensions of the image to display as a function of the resolution of the screen
        Display display = ((WindowManager) activity.getBaseContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        widthScale = 1200;
        heightScale = 2000;
        bMapScaledWidth = (bMap.getWidth() * display.getWidth()) / widthScale;
        bMapScaledHeight = (bMap.getHeight() * display.getHeight()) / heightScale;

        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, bMapScaledWidth, bMapScaledHeight, true);
        image.setImageBitmap(bMapScaled);
        text.setText(fullName);
        checkbox.setChecked((new StudentItemModel(u)).isSelected());


        return convertView;
    }


	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	
	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}
}
