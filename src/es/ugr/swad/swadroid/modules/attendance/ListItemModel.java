package es.ugr.swad.swadroid.modules.attendance;


public class ListItemModel {
	private int imageId;
	private String name;
	private boolean selected;

	public ListItemModel(String name, int imageId) {
		this.imageId = imageId;
		this.name = name;
		selected = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

}
