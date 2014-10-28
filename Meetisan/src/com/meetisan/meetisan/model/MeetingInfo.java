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
	/** meet determin start time */
	private String determinStartTime = null;
	/** meet determin end time */
	private String determinEndTime = null;
	/** meet start time 1 */
	private String startTime1 = null;
	/** meet end time 1 */
	private String endTime1 = null;
	/** meet start time 2 */
	private String startTime2 = null;
	/** meet end time 2 */
	private String endTime2 = null;
	/** meet start time 3 */
	private String startTime3 = null;
	/** meet end time 3 */
	private String endTime3 = null;
	/** meet create date */
	private String createDate = null;
	/** meet join status, 0:已参加; 1：未参加; 2：当前用户为meeting创建人; 3: 拒绝邀请 ; 4: 收到邀请; */
	private int joinStatus = 0;
	/** meet status */
	private int status = 0;
	/** meet join */
	private boolean canJoin = false;
	/** meet time set type: 1: I'll choose; 2: Let them choose */
	private int timeSetType = 1;
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

	public String getDeterminStartTime() {
		return determinStartTime;
	}

	public void setDeterminStartTime(String determinStartTime) {
		this.determinStartTime = determinStartTime;
	}

	public String getDeterminEndTime() {
		return determinEndTime;
	}

	public void setDeterminEndTime(String determinEndTime) {
		this.determinEndTime = determinEndTime;
	}

	public String getStartTime1() {
		return startTime1;
	}

	public void setStartTime1(String startTime1) {
		this.startTime1 = startTime1;
	}

	public String getEndTime1() {
		return endTime1;
	}

	public void setEndTime1(String endTime1) {
		this.endTime1 = endTime1;
	}

	public String getStartTime2() {
		return startTime2;
	}

	public void setStartTime2(String startTime2) {
		this.startTime2 = startTime2;
	}

	public String getEndTime2() {
		return endTime2;
	}

	public void setEndTime2(String endTime2) {
		this.endTime2 = endTime2;
	}

	public String getStartTime3() {
		return startTime3;
	}

	public void setStartTime3(String startTime3) {
		this.startTime3 = startTime3;
	}

	public String getEndTime3() {
		return endTime3;
	}

	public void setEndTime3(String endTime3) {
		this.endTime3 = endTime3;
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

	public boolean isCanJoin() {
		return canJoin;
	}

	public void setCanJoin(boolean canJoin) {
		this.canJoin = canJoin;
	}

	public String getLogoUri() {
		return logoUri;
	}

	public void setLogoUri(String logoUri) {
		this.logoUri = logoUri;
	}

	public int getTimeSetType() {
		return timeSetType;
	}

	public void setTimeSetType(int timeSetType) {
		this.timeSetType = timeSetType;
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
