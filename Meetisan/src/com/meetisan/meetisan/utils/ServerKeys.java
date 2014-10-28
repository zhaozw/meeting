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
	public static final String KEY_MEET_COUNT = "MeetingCount";
	public static final String KEY_UPCOMING_MEET = "UpcomingMeeting";
	public static final String KEY_USER = "User";
	public static final String KEY_TOP_TAGS = "TopTags";
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_FAILED = 1;
	private static final String API_TUSER = "/api/tuser/";
	private static final String API_TTAG = "/api/ttag/";
	private static final String API_TUSER_TAG = "/api/tUserTag/";
	private static final String API_TMEET = "/api/tmeeting/";
	private static final String API_TMEET_ENDORSE = "/api/tMeeting/";
	private static final String API_TNOTIFICATION = "/api/tnotification/";
	private static final String API_TMEET_MEMBER = "/api/tmeetingmember/";
	private static final String API_TMEET_MEMBER_ENDORSE = "/api/tMeetingMember/";
	private static final String API_TTAG_CATEGORY = "/api/tTagCategory/";
	private static final String API_TAG_MOMENT = "/api/ttagmoment/";
	private static final String API_TAG_CREATE_APPLY = "/api/tTagCreateApply/";
	private static final String API_TREPORT = "/api/treport/";

	// URL
	/** http://{域名}/api/tuser/Login/ */
	public static final String FULL_URL_LOGIN = SERVER_ADDRESS + API_TUSER + "Login";
	/** http://{域名}/api/tuser/LoginOut/{用户ID} */
	public static final String FULL_URL_LOGOUT = SERVER_ADDRESS + API_TUSER + "LoginOut";
	public static final String FULL_URL_SEND_CODE = SERVER_ADDRESS + API_TUSER + "SendActivationCode";
	public static final String FULL_URL_CHECK_CODE = SERVER_ADDRESS + API_TUSER + "CheckActivationCode";
	public static final String FULL_URL_REGISTER = SERVER_ADDRESS + API_TUSER + "Register";
	/** http://{域名}/api/tUser/UpdatePassword/ */
	public static final String FULL_URL_UPDATE_USER_PWD = SERVER_ADDRESS + API_TUSER + "UpdatePassword";
	/** http://{域名}/api/tuser/Update/ */
	public static final String FULL_URL_UPDATE_USER_INFO = SERVER_ADDRESS + API_TUSER + "Update";
	/** http://{域名}/api/tuser/updatelocation/{用户ID}/?lat={当前用户经度}&lon={当前用户经度} */
	public static final String FULL_URL_UPDATE_LOCATION = SERVER_ADDRESS + API_TUSER + "updatelocation";
	/** http://{域名}/api/tuser/Get/{用户ID} */
	public static final String FULL_URL_GET_USER_INFO = SERVER_ADDRESS + API_TUSER + "Get";
	/**
	 * http://{域名}/api/ttag/GetUserTag/{userId}/?pageindex={页码}&pagesize={每页数量}&
	 * name={按tag的名字搜索，否则请传空}
	 */
	public static final String FULL_URL_GET_USER_TAG = SERVER_ADDRESS + API_TTAG + "GetUserTag";
	/** http://{域名}/api/ttag/get/{tag的ID}/?UserID={当前用户ID，用于检测用户是否添加过该tag} */
	public static final String FULL_URL_GET_TAG_INFO = SERVER_ADDRESS + API_TTAG + "get";
	/** http://{域名}/api/tTagCategory/GetList/?pageindex={页码}&pagesize={每页数量} */
	public static final String FULL_URL_GET_TAG_LIST = SERVER_ADDRESS + API_TTAG_CATEGORY + "GetList";
	/** http://{域名}/api/tTagCreateApply/Add/ */
	public static final String FULL_URL_CREATE_TAG = SERVER_ADDRESS + API_TAG_CREATE_APPLY + "Add";
	/**
	 * http://{域名}/api/tuser/getlist/{当前用户ID}/?pageindex={页码}&pagesize={每页数量}&
	 * lat={当前用户经度}&lon={当前用户纬度}&tagIDs={按tag筛选时传入tag
	 * id，ID以逗号隔开，否则传空.例：3,4}&name={按用户名称搜索传入用户名，否则传空}
	 */
	public static final String FULL_URL_GET_UESR_LIST = SERVER_ADDRESS + API_TUSER + "getlist";
	/**
	 * http://{域名}/api/tmeeting/getlist/?pageindex={页码}&pagesize={每页数量}&
	 * ordertype={排序方式，0：按距离升序 1：按开始时间升序
	 * 2：按创建时间降序}&lat={当前用户经度}&lon={当前用户经度}&tagIDs={按tag筛选时传入tag
	 * id，ID以逗号隔开，否则传空.例：3,4}&title={按meeting名称搜索时传入meeting title字段，否则传空}
	 */
	public static final String FULL_URL_GET_MEET_LIST = SERVER_ADDRESS + API_TMEET + "getlist";
	/** http://{域名}/api/tmeeting/add/ */
	public static final String FULL_URL_MEETING_ADD = SERVER_ADDRESS + API_TMEET + "add/";
	/**
	 * http://{域名}/api/ttag/GetByCategory/{tag分类ID}/?pageindex={页码}&pagesize={
	 * 每页数量}&name={按tag的名字搜索，否则请传空}
	 */
	public static final String FULL_URL_GET_TAGS_BY_CATEGORY = SERVER_ADDRESS + API_TTAG + "GetByCategory";
	/**
	 * http://{域名}/api/tmeeting/get/{meeting的ID}/?UserID={当前用户ID，
	 * 用于判断是否参加了该meeting}
	 */
	public static final String FULL_URL_GET_MEET_INFO = SERVER_ADDRESS + API_TMEET + "get";
	/**
	 * http://{域名}/api/tmeetingmember/getlist/{meeting的ID}/?pageindex={页码}&
	 * pagesize={每页数量}
	 */
	public static final String FULL_URL_GET_MEET_MEMBER = SERVER_ADDRESS + API_TMEET_MEMBER + "getlist";
	/** http://{域名}/api/tMeetingMember/add/ */
	public static final String FULL_URL_ATTEND_MEET = SERVER_ADDRESS + API_TMEET_MEMBER + "add";
	/** http://{域名}/api/tMeetingMember/Delete/ */
	public static final String FULL_URL_CANCEL_ATTEND_MEET = SERVER_ADDRESS + API_TMEET_MEMBER + "Delete";
	/**
	 * http://{域名}/api/tmeeting/GetUserMeetingList/{用户ID}/?pageindex={页码}&
	 * pagesize={每页数量}&lat={当前用户经度}&lon={当前用户经度}
	 */
	public static final String FULL_URL_GET_USER_MEET_LIST = SERVER_ADDRESS + API_TMEET + "GetUserMeetingList";
	/**
	 * http://{域名}/api/tuser/GetConnection/{当前用户ID}/?pageindex={页码}&pagesize={
	 * 每页数量}
	 */
	public static final String FULL_URL_GET_USER_CONNECTION_LIST = SERVER_ADDRESS + API_TUSER + "GetConnection";
	/**
	 * http://{域名}/api/tmeeting/GetUpcoming/{用户ID}/?pageindex={页码}&pagesize={
	 * 每页数量}&lat={当前用户经度}&lon={当前用户经度}
	 */
	public static final String FULL_URL_GET_UPCOMING_MEET = SERVER_ADDRESS + API_TMEET + "GetUpcoming";
	/** http://{域名}/api/ttag/DeleteUserTag/{传入MyTags页面获取到的UserTagID字段} */
	public static final String FULL_URL_DEL_TAG = SERVER_ADDRESS + API_TTAG + "DeleteUserTag";
	/**
	 * http://{域名}/api/tNotification/getlist/{当前用户ID}/?pageindex={页码}&pagesize={
	 * 每页数量}
	 */
	public static final String FULL_URL_GET_NOTIFICATION = SERVER_ADDRESS + API_TNOTIFICATION + "getlist";
	/**
	 * http://{域名}/api/tuser/getlist/{当前用户ID}/?pageindex={页码}&pagesize={每页数量}&
	 * lat={当前用户经度}&lon={当前用户纬度}&tagIDs={当前TagID}
	 */
	public static final String FULL_URL_GET_CONNECTTED_PEOPLE = SERVER_ADDRESS + API_TUSER + "getlist";
	/**
	 * http://{域名}/api/tmeeting/GetUserMeetingList/{用户ID}/?pageindex={页码}&
	 * pagesize={每页数量}&lat={当前用户经度}&lon={当前用户经度}&tagIDs={当前TagID}
	 */
	public static final String FULL_URL_GET_ATTENDED_MEET = SERVER_ADDRESS + API_TMEET + "GetUserMeetingList";
	/**
	 * http://{域名}/api/tmeeting/GetTagMeeting/?pageindex={页码}&pagesize={每页数量}&
	 * lat={当前用户经度}&lon={当前用户经度}&tagIDs={当前TagID}
	 */
	public static final String FULL_URL_GET_ASSOCIATE_MEET = SERVER_ADDRESS + API_TMEET + "GetTagMeeting";
	/**
	 * http://{域名}/api/ttagmoment/getlist/{Tag的ID}/?pageindex={页码}&pagesize={
	 * 每页数量}
	 */
	public static final String FULL_URL_GET_TAG_MOMENT_LIST = SERVER_ADDRESS + API_TAG_MOMENT + "getlist";
	/** http://{域名}/api/ttagmoment/add */
	public static final String FULL_URL_ADD_TAG_MOMENT = SERVER_ADDRESS + API_TAG_MOMENT + "add";
	/**
	 * http://{域名}/api/ttag/searchtag/?name={要搜索的关键字}&pageindex={页码}&pagesize={
	 * 每页数量}
	 */
	public static final String FULL_URL_SEARCH_TAG = SERVER_ADDRESS + API_TTAG + "searchtag";
	/**
	 * http://meetisanapp.azurewebsites.net/api/tUserTag/Add/ (UserID ：对应用户ID
	 * TagID：对应要加入的tag)
	 */
	public static final String FULL_URL_USER_ADD_TAG = SERVER_ADDRESS + API_TUSER_TAG + "Add";
	/** http://{域名}/api/treport/add */
	public static final String FULL_URL_SEND_REPORT = SERVER_ADDRESS + API_TREPORT + "add";
	/** http://{域名}/api/tNotification/UpdateStatus */
	public static final String FULL_URL_UPDATE_NOTIFICATION_STATUS = SERVER_ADDRESS + API_TNOTIFICATION
			+ "UpdateStatus";

	/**
	 * http://{域名}/api/tMeeting/GetEndorseMeeting/{当前用户ID}/?pageindex={页码}&
	 * pagesize={每页数量}
	 */
	public static final String FULL_URL_ENDORSE_MEET = SERVER_ADDRESS + API_TMEET_ENDORSE + "GetEndorseMeeting";

	/**
	 * http://{域名}/api/tMeetingMember/GetEndorseMember/{Meeting的标识ID}/?pageindex
	 * ={页码}&pagesize={每页数量}&userid={当前用户ID}
	 */
	public static final String FULL_URL_ENDORSE_MEMBER_LIST = SERVER_ADDRESS + API_TMEET_MEMBER_ENDORSE
			+ "GetEndorseMember";
	/** http://{域名}/api/tMeetingMember/EndorseMember/ */
	public static final String FULL_URL_ENDORSE_MEMBERS = SERVER_ADDRESS + API_TMEET_MEMBER_ENDORSE + "EndorseMember";

	// Keys
	public static final String KEY_ID = "ID";
	public static final String KEY_EMAIL = "Email";
	public static final String KEY_NAME = "Name";
	public static final String KEY_PASSWORD = "Password";
	public static final String KEY_CODE_ID = "CodeID";
	public static final String KEY_CODE = "Code";
	public static final String KEY_AVATAR = "Avatar";
	public static final String KEY_BG = "BackGroundImg";
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
	public static final String KEY_USER_TAG_ID = "UserTagID";
	public static final String KEY_USER_TAG_IDS = "UserTagIDs";
	public static final String KEY_USER_ID = "UserID";
	public static final String KEY_USER_NAME = "UserName";
	public static final String KEY_CATEGORY_ID = "CategoryID";
	public static final String KEY_TITLE = "Title";
	public static final String KEY_LOGO = "Logo";
	public static final String KEY_IMAGE = "Image";
	public static final String KEY_ENDORSEMENTS = "Endorsements";
	public static final String KEY_PEOPLES = "Peoples";
	public static final String KEY_MEETINGS = "Meetings";
	public static final String KEY_MEETING = "Meeting";
	public static final String KEY_MEETING_ID = "MeetingID";
	public static final String KEY_MEETING_USER_ID = "MeetingUserID";
	public static final String KEY_LINK = "Link";
	public static final String KEY_DESCRIPTION = "Description";
	public static final String KEY_FOLLOW_STATUS = "FollowStatus";
	public static final String KEY_JOIN_STATUS = "JoinStatus";
	public static final String KEY_DISTANCE = "Distance";
	public static final String KEY_ADDRESS = "Address";
	public static final String KEY_CREATE_USER_ID = "CreateUserID";
	public static final String KEY_DETERMIN_START_TIME = "DetermineStartTime";
	public static final String KEY_DETERMIN_END_TIME = "DetermineEndTime";
	public static final String KEY_START_TIME1 = "StartTime1";
	public static final String KEY_END_TIME1 = "EndTime1";
	public static final String KEY_START_TIME2 = "StartTime2";
	public static final String KEY_END_TIME2 = "EndTime2";
	public static final String KEY_START_TIME3 = "StartTime3";
	public static final String KEY_END_TIME3 = "EndTime3";
	public static final String KEY_REPORT_OBJECT_ID = "ReportObjectID";
	public static final String KEY_TYPE = "Type";
	public static final String KEY_IS_PRIVATE = "IsPrivate";
	public static final String KEY_CAN_JOIN = "CanJoin";
	public static final String KEY_REPORT_ID = "ReportObjectID";
	public static final String KEY_REPORT_TYPE = "Type";
	public static final String KEY_TIME_SET_TYPE = "TimeSetType";
}
