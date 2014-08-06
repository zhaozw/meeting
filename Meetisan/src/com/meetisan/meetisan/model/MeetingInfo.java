package com.meetisan.meetisan.model;

import java.util.ArrayList;
import java.util.List;

public class MeetingInfo {
	/** meet id */
	private long id = 0;
	/** meet create user id */
	private long createUserId = 0;
	/** meet name */
	private String title = null;
	/** meet description */
	private String description = null;
	/** meet address */
	private String address = null;
	/** meet logo Uri */
	// private Bitmap logo = null;
	private String logoUri = null;
	/** people longitude */
	private double longitude = 0.0f;
	/** people latitude */
	private double latitude = 0.0f;
	/** meet distance */
	private double distance = 0.0;
	/** meet start time */
	private String startTime = null;
	/** meet end time */
	private String endTime = null;
	/** meet create date */
	private String createDate = null;
	/** meet join status, 0:已参加; 1：未参加; 2：当前用户为meeting创建人 */
	private int joinStatus = 0;
	/** meet status */
	private int status = 0;
	/** meet tags */
	private List<TagInfo> tags = new ArrayList<TagInfo>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getJoinStatus() {
		return joinStatus;
	}

	public void setJoinStatus(int joinStatus) {
		this.joinStatus = joinStatus;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLogoUri() {
		return logoUri;
	}

	public void setLogoUri(String logoUri) {
		this.logoUri = logoUri;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public List<TagInfo> getTags() {
		return tags;
	}

	public void setTags(List<TagInfo> tags) {
		this.tags = tags;
	}

	public boolean addTag(TagInfo tag) {
		if (tag == null) {
			return false;
		}
		return this.tags.add(tag);
	}
}
