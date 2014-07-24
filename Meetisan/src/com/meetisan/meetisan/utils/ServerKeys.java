package com.meetisan.meetisan.utils;

public class ServerKeys {
	public static final String SERVER_ADDRESS = "http://meetisanapp.azurewebsites.net";
	public static final int SERVER_PORT = 80;
	
	public static final int PAGE_SIZE = 20;

	public static final String KEY_STATUS = "Status";
	public static final String KEY_STATUS_CODE = "StatusCode";
	public static final String KEY_MSG = "Msg";
	public static final String KEY_DATA = "Data";
	public static final String KEY_DATA_LIST = "DataList";
	public static final String KEY_TOTAL_COUNT = "TotalCount";
	public static final String KEY_USER = "User";
	public static final String KEY_TOP_TAGS = "TopTags";
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_FAILED = 1;
	private static final String API_TUSER = "/api/tuser/";
	private static final String API_TTAG = "/api/ttag/";

	// URL
	public static final String FULL_URL_LOGIN = SERVER_ADDRESS + API_TUSER + "Login";
	public static final String FULL_URL_SEND_CODE = SERVER_ADDRESS + API_TUSER + "SendActivationCode";
	public static final String FULL_URL_CHECK_CODE = SERVER_ADDRESS + API_TUSER + "CheckActivationCode";
	public static final String FULL_URL_REGISTER = SERVER_ADDRESS + API_TUSER + "Register";
	public static final String FULL_URL_GET_USER_INFO = SERVER_ADDRESS + API_TUSER + "Get";
	/**	http://{域名}/api/ttag/GetUserTag/{userId}/?pageindex={页码}&pagesize={每页数量}&name={按tag的名字搜索，否则请传空} */
	public static final String FULL_URL_GET_USER_TAG =  SERVER_ADDRESS + API_TTAG + "GetUserTag";

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
	public static final String KEY_ENDORSEMENTS = "Endorsements";
	public static final String KEY_PEOPLES = "Peoples";
	public static final String KEY_MEETINGS = "Meetings";
}
