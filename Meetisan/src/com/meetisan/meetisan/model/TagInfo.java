package com.meetisan.meetisan.model;

import android.graphics.Bitmap;
import android.nfc.Tag;

public class TagInfo {

	/** tag id */
	private long id = 0;
	/** tag category id */
	private long categroyId = 0;
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

	/** tag endorsed times */
	private long endorsed = 0;
	/** tag people ?? */
	private long people = 0;
	/** tag meetings ?? */
	private long meetings = 0;

	/** tag host */
	private TagHost tagHost = null;
	/** tag moment */
	private TagMoment tagMoment = null;
	
	public TagInfo() {
		tagHost = new TagHost();
		tagMoment = new TagMoment();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCategroyId() {
		return categroyId;
	}

	public void setCategroyId(long categroyId) {
		this.categroyId = categroyId;
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

	public long getEndorsed() {
		return endorsed;
	}

	public void setEndorsed(long endorsed) {
		this.endorsed = endorsed;
	}

	public long getPeople() {
		return people;
	}

	public void setPeople(long people) {
		this.people = people;
	}

	public long getMeetings() {
		return meetings;
	}

	public void setMeetings(long meetings) {
		this.meetings = meetings;
	}

	public TagHost getTagHost() {
		return tagHost;
	}

	public void setTagHost(TagHost tagHost) {
		this.tagHost = tagHost;
	}

	public TagMoment getTagMoment() {
		return tagMoment;
	}

	public void setTagMoment(TagMoment tagMoment) {
		this.tagMoment = tagMoment;
	}

}
