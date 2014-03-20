package es.ugr.swad.swadroid.gui;

public class ImageListItem {
	private int imageId;
	private String title;
	
	public ImageListItem(int imageId, String title) {
		this.imageId = imageId;
		this.title = title;
	}
	
	public int getImageId() {
		return imageId;
	}
	
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "ImageListItem [imageId=" + imageId + ", title=" + title + "]";
	}	
}
