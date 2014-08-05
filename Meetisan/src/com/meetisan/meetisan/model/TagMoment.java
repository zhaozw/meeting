package com.meetisan.meetisan.model;


public class TagMoment {

	/** tag moment id */
	private long id = 0;
	/** tag id */
	private long tagId = 0;
	/** tag moment image */
	// private Bitmap image = null;
	private String imageUri = null;
	/** tag moment title */
	private String title = null;
	/** tag moment create user id */
	private long userId = 0;
	/** tag moment create time */
	private String createDate = null;

	public TagMoment() {

	}

	public TagMoment(long id, long tagId, String imageUri, String title, long userId,
			String createDate) {
		this.id = id;
		this.tagId = tagId;
		this.imageUri = imageUri;
		this.title = title;
		this.userId = userId;
		this.createDate = createDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTagId() {
		return tagId;
	}

	public void setTagId(long tagId) {
		this.tagId = tagId;
	}

	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

}
