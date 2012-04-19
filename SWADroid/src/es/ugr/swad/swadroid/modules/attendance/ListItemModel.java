package es.ugr.swad.swadroid.modules.attendance;


public class ListItemModel implements Comparable<ListItemModel> {
	private int imageId;
	private String Firstname;
	private String Surname1;
	private String Surname2;
	private boolean selected;

	public ListItemModel(String firstname, String surname1, String surname2, int imageId) {
		this.imageId = imageId;
		this.Firstname = firstname;
		this.Surname1 = surname1;
		this.Surname2 = surname2;
		selected = false;
	}

	public String toString() {
		return this.Surname1 + " " + this.Surname2 + ", " + this.Firstname;
	} 

	public String getFirstname() {
		return Firstname;
	}

	public void setFirstname(String firstname) {
		Firstname = firstname;
	}

	public String getSurname1() {
		return Surname1;
	}

	public void setSurname1(String surname1) {
		Surname1 = surname1;
	}

	public String getSurname2() {
		return Surname2;
	}

	public void setSurname2(String surname2) {
		Surname2 = surname2;
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

	@Override
	public int compareTo(ListItemModel item) {
		if(this.Surname1.compareToIgnoreCase(item.Surname1) == 0) {
			if(this.Surname2.compareToIgnoreCase(item.Surname2) == 0) {
				return this.Firstname.compareTo(item.Firstname);
			} else {
				return this.Surname2.compareToIgnoreCase(item.Surname2);
			}
		} else {
			return this.Surname1.compareToIgnoreCase(item.Surname1);
		}
	}
}
