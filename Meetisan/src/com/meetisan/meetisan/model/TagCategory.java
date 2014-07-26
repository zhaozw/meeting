package com.meetisan.meetisan.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class TagCategory {
	/** tag category id */
	private long id = 0;
	/** tag category name */
	private String title = null;
	/** tag category logo */
	private Bitmap logo = null;
	/** tags in this category */
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
