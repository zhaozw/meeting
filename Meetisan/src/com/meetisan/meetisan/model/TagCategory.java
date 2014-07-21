package com.meetisan.meetisan.model;

import java.util.ArrayList;
import java.util.List;

public class TagCategory {
	/** tag category id */
	private long id = 0;
	/** tag category name */
	private String name = null;
	/** tags in this category */
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

	public List<TagInfo> getTags() {
		return tags;
	}

	public void setTags(List<TagInfo> tags) {
		this.tags = tags;
	}

	public void addTags(TagInfo tags) {
		this.tags.add(tags);
	}

}
