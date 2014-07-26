package com.meetisan.meetisan.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class MeetingInfo {
	/** people id */
	private long id = 0;
	/** people name */
	private String title = null;
	/** people icon */
	private Bitmap logo = null;
	/** people distance */
	private double distance = 0.0;
	/** people tags */
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

	public Bitmap getLogo() {
		return logo;
	}

	public void setLogo(Bitmap logo) {
		this.logo = logo;
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
