package com.meetisan.meetisan.utils;

public class ServerKeys {
	public static final String SERVER_ADDRESS = "http://meetisanapp.azurewebsites.net";
	public static final int SERVER_PORT = 80;
	
	public static final int PAGE_SIZE = 20;

	public static final String KEY_STATUS = "Status";
	public static final String KEY_STATUS_CODE = "StatusCode";
	public static final String KEY_MSG = "Msg";
	public static final String KEY_DATA = "Data";
	public static final String KEY_TAG = "Tag";
	public static final String KEY_TAGS = "Tags";
	public static final String KEY_TAG_HOST = "TagHost";
	public static final String KEY_TAG_MOMENTS = "Moments";
	public static final String KEY_DATA_LIST = "DataList";
	public static final String KEY_TOTAL_COUNT = "TotalCount";
	public static final String KEY_USER = "User";
	public static final String KEY_TOP_TAGS = "TopTags";
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_FAILED = 1;
	private static final String API_TUSER = "/api/tuser/";
	private static final String API_TTAG = "/api/ttag/";
	private static final String API_TMEET = "/api/tmeeting/";
	private static final String API_TTAG_CATEGORY = "/api/tTagCategory/";

	// URL
	public static final String FULL_URL_LOGIN = SERVER_ADDRESS + API_TUSER + "Login";
	public static final String FULL_URL_SEND_CODE = SERVER_ADDRESS + API_TUSER + "SendActivationCode";
	public static final String FULL_URL_CHECK_CODE = SERVER_ADDRESS + API_TUSER + "CheckActivationCode";
	public static final String FULL_URL_REGISTER = SERVER_ADDRESS + API_TUSER + "Register";
	public static final String FULL_URL_GET_USER_INFO = SERVER_ADDRESS + API_TUSER + "Get";
	/**	http://{域名}/api/ttag/GetUserTag/{userId}/?pageindex={页码}&pagesize={每页数量}&name={按tag的名字搜索，否则请传空} */
	public static final String FULL_URL_GET_USER_TAG =  SERVER_ADDRESS + API_TTAG + "GetUserTag";
	/** http://{域名}/api/ttag/get/{tag的ID}/?UserID={当前用户ID，用于检测用户是否添加过该tag} */
	public static final String FULL_URL_GET_TAG_INFO =  SERVER_ADDRESS + API_TTAG + "get";
	/** http://{域名}/api/tTagCategory/GetList/?pageindex={页码}&pagesize={每页数量} */
	public static final String FULL_URL_GET_TAG_LIST =  SERVER_ADDRESS + API_TTAG_CATEGORY + "GetList";
	/** http://{域名}/api/tuser/getlist/{当前用户ID}/?pageindex={页码}&pagesize={每页数量}&lat={当前用户经度}&lon={当前用户纬度}&tagIDs={按tag筛选时传入tag id，ID以逗号隔开，否则传空.例：3,4}&name={按用户名称搜索传入用户名，否则传空} */
	public static final String FULL_URL_GET_UESR_LIST = SERVER_ADDRESS + API_TUSER + "getlist";
	/** http://{域名}/api/tmeeting/getlist/?pageindex={页码}&pagesize={每页数量}&ordertype={排序方式，0：按距离升序 1：按开始时间升序 2：按创建时间降序}&lat={当前用户经度}&lon={当前用户经度}&tagIDs={按tag筛选时传入tag id，ID以逗号隔开，否则传空.例：3,4}&title={按meeting名称搜索时传入meeting title字段，否则传空} */
	public static final String FULL_URL_GET_MEET_LIST = SERVER_ADDRESS + API_TMEET + "getlist";
	/** http://{域名}/api/ttag/GetByCategory/{tag分类ID}/?pageindex={页码}&pagesize={每页数量}&name={按tag的名字搜索，否则请传空} */
	public static final String FULL_URL_GET_TAGS_BY_CATEGORY = SERVER_ADDRESS + API_TTAG + "GetByCategory";
	
	// Keys
	public static final String KEY_ID = "ID";
	public static final String KEY_EMAIL = "Email";
	public static final String KEY_NAME = "Name";
	public static final String KEY_PASSWORD = "Password";
	public static final String KEY_AVATAR = "Avatar";
	public static final String KEY_UNIVERSITY = "University";
	public static final String KEY_SIGNATURE = "Signature";
	public static final String KEY_CITY = "City";
	public static final String KEY_AGE = "Age";
	public static final String KEY_GENDER = "Gender";
	public static final String KEY_EXPERIENCE = "Experience";
	public static final String KEY_EDUCATION = "Education";
	public static final String KEY_SKILLS = "Skills";
	public static final String KEY_LON = "Lon";
	public static final String KEY_LAT = "Lat";
	public static final String KEY_CREATE_DATE = "CreateDate";
	public static final String KEY_REG_ID = "RegistrationID";
	public static final String KEY_TAG_ID = "TagID";
	public static final String KEY_USER_ID = "UserID";
	public static final String KEY_CATEGORY_ID = "CategoryID";
	public static final String KEY_TITLE = "Title";
	public static final String KEY_LOGO = "Logo";
	public static final String KEY_IMAGE = "Image";
	public static final String KEY_ENDORSEMENTS = "Endorsements";
	public static final String KEY_PEOPLES = "Peoples";
	public static final String KEY_MEETINGS = "Meetings";
	public static final String KEY_MEETING = "Meeting";
	public static final String KEY_LINK = "Link";
	public static final String KEY_DESCRIPTION = "Description";
	public static final String KEY_FOLLOW_STATUS = "FollowStatus";
	public static final String KEY_DISTANCE = "Distance";
}
