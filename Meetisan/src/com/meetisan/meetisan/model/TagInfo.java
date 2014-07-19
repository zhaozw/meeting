package com.meetisan.meetisan.model;

import android.graphics.Bitmap;

public class TagInfo {

	/** tag id */
	private long id = 0;
	/** tag name */
	private String name = null;
	/** tag title */
	private String title = null;
	/** tag description */
	private String description = null;
	/** tag icon */
	private Bitmap logo = null;
	/** tag create date (such as: 2014-07-07T07:34:01) */
	private String createDate = null;
	/** tag state, 0 or 1 */
	private int state = 0;

	/** tag create user (host) id */
	private long hostId = 0;
	/** tag create user (host) name */
	private String hostName = null;
	/** tag endorsed times */
	private long endorsed = 0;
	/** tag moment */
	private TagMoment tagMoment = null;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Bitmap getLogo() {
		return logo;
	}

	public void setLogo(Bitmap logo) {
		this.logo = logo;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getHostId() {
		return hostId;
	}

	public void setHostId(long hostId) {
		this.hostId = hostId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public long getEndorsed() {
		return endorsed;
	}

	public void setEndorsed(long endorsed) {
		this.endorsed = endorsed;
	}

	public TagMoment getTagMoment() {
		return tagMoment;
	}

	public void setTagMoment(TagMoment tagMoment) {
		this.tagMoment = tagMoment;
	}

}
