package com.meetisan.meetisan.model;

import java.util.ArrayList;
import java.util.List;

public class PeopleInfo {
	/** people id */
	private long id = 0;
	/** people email */
	private String email = null;
	/** people password */
	private String pwd = null;
	/** people name */
	private String name = null;
	// /** people icon */
	// private Bitmap avatar = null;
	/** people avatar Uri */
	private String avatarUri = null;
	/** people background image Uri */
	private String bgUri = null;
	// private Bitmap bgImage = null;
	/** people signature */
	private String signature = null;
	/** people college */
	private String university = null;
	/** people city */
	private String city = null;
	/** people age */
	private String birthday = null;
	/** people gender */
	private int gender = 0;
	/** people experience */
	private String experience = null;
	/** people education */
	private String education = null;
	/** people skills */
	private String skills = null;
	/** people longitude */
	private float longitude = 0.0f;
	/** people latitude */
	private float latitude = 0.0f;
	/** people status:  0: Invited; 1: Accepted; 2: Declined*/
	private int status = 0;
	/** people create date [2014-07-23T02:40:47.786] */
	private String createDate = null;
	/** people registration ID */
	private String regId = null;
	/** people distance */
	private double distance = 0.0;
	/** people tags */
	private List<TagInfo> topTags = new ArrayList<TagInfo>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatarUri() {
		return avatarUri;
	}

	public void setAvatarUri(String avatarUri) {
		this.avatarUri = avatarUri;
	}

	public String getBgUri() {
		return bgUri;
	}

	public void setBgUri(String bgUri) {
		this.bgUri = bgUri;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
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

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public List<TagInfo> getTopTags() {
		return topTags;
	}

	public void setTopTags(List<TagInfo> topTags) {
		this.topTags = topTags;
	}

	public boolean addTopTag(TagInfo tagInfo) {
		return this.topTags.add(tagInfo);
	}
	
	public void clearTopTags() {
		this.topTags.clear();
	}

}
