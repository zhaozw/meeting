package com.meetisan.meetisan.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class PeopleInfo {
	/** people id */
	private long id = 0;
	/** people name */
	private String name = null;
	/** people icon */
	private Bitmap logo = null;
	/** people college */
	private String college = null;
	/** people distance */
	private long distance = 0;
	/** people tags */
	private List<TagInfo> tags = new ArrayList<TagInfo>();

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

	public Bitmap getLogo() {
		return logo;
	}

	public void setLogo(Bitmap logo) {
		this.logo = logo;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public List<TagInfo> getTags() {
		return tags;
	}

	public void setTags(List<TagInfo> tags) {
		this.tags = tags;
	}

	public boolean addTags(TagInfo tag) {
		if (tag == null) {
			return false;
		}
		return this.tags.add(tag);
	}
}
