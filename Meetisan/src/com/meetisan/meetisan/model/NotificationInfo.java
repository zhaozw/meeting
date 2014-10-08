package com.meetisan.meetisan.model;

public class NotificationInfo {
	private long id;
	private long userID;
	/**
	 * 当Type为2，3时，此ID为tag的ID 当Type为1时，此ID为meeting的ID 当Type为4 ，5时，此ID为操作用户的ID
	 */
	private long reportObjectID;
	private String title;
	private String content;
	/**
	 * Type的含义： 1：收到meeting邀请 2：tag创建申请通过 3：tag创建申请不通过 4：有人参加我创建的meeting
	 * 5：有人拒绝我的meeting邀请
	 */
	private int type;
	public static final int TYPE_MEETING_INVITATION = 1;
	public static final int TYPE_TAG_CREATE_SUCCESS = 2;
	public static final int TYPE_TAG_CREATE_FAILED = 3;
	public static final int TYPE_MEETING_INVITE_JOIN = 4;
	public static final int TYPE_MEETING_INVITE_REFUSE = 5;
	/** 1表示已读，0表示未读 */
	private int status;
	private String createDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public long getReportObjectID() {
		return reportObjectID;
	}

	public void setReportObjectID(long reportObjectID) {
		this.reportObjectID = reportObjectID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
}
