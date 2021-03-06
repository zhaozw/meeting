package com.meetisan.meetisan.model;

import java.util.ArrayList;
import java.util.List;

public class TagInfo {

	/** tag id */
	private long id = 0;
	/** tag user tag id, for delete user tag */
	private long userTagId = 0;
	/** tag category id */
	private long categroyId = 0;
	/** tag name */
	// private String name = null;
	/** tag title */
	private String title = null;
	/** tag description */
	private String description = null;
	/** tag logo Uri */
	// private Bitmap logo = null;
	private String logoUri = null;
	/** tag create date (such as: 2014-07-07T07:34:01) */
	private String createDate = null;
	/** tag state, 0 or 1 */
	private int state = 0;
	/** tag follow, 0 or 1 */
	private int follow = 0;

	/** tag endorsed times */
	private long endorsed = 0;
	/** tag people ?? */
	private long people = 0;
	/** tag meetings ?? */
	private long meetings = 0;
	/** tag link */
	private String link = null;

	/** is endorsed, for user meeting endorsement */
	private boolean isEndorsed = false;

	/** tag host */
	private TagHost tagHost;
	/** tag moment */
	private List<TagMoment> tagMoments;

	public TagInfo() {
		tagMoments = new ArrayList<TagMoment>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserTagId() {
		return userTagId;
	}

	public void setUserTagId(long userTagId) {
		this.userTagId = userTagId;
	}

	public long getCategroyId() {
		return categroyId;
	}

	public void setCategroyId(long categroyId) {
		this.categroyId = categroyId;
	}

	// public String getName() {
	// return name;
	// }
	//
	// public void setName(String name) {
	// this.name = name;
	// }

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

	public String getLogoUri() {
		return logoUri;
	}

	public void setLogoUri(String logoUri) {
		this.logoUri = logoUri;
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

	public int getFollow() {
		return follow;
	}

	public void setFollow(int follow) {
		this.follow = follow;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isEndorsed() {
		return isEndorsed;
	}

	public void setEndorsed(boolean isEndorsed) {
		this.isEndorsed = isEndorsed;
	}

	public TagHost getTagHost() {
		return tagHost;
	}

	public void setTagHost(TagHost tagHost) {
		this.tagHost = tagHost;
	}

	public List<TagMoment> getTagMoments() {
		return tagMoments;
	}

	public void setTagMoments(List<TagMoment> tagMoments) {
		this.tagMoments = tagMoments;
	}

	public boolean addTagMoment(TagMoment tagMoment) {
		return this.tagMoments.add(tagMoment);
	}
}
