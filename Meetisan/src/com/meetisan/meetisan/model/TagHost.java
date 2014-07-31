package com.meetisan.meetisan.model;

public class TagHost {
	/** tag create user (host) id */
	private long hostId = 0;
	/** tag create user (host) name */
	private String hostName = null;

	public TagHost() {

	}

	public TagHost(long hostId, String hostName) {
		this.hostId = hostId;
		this.hostName = hostName;
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

}
