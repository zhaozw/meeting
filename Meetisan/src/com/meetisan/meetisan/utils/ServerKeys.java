package com.meetisan.meetisan.utils;

public class ServerKeys {
	public static final String SERVER_ADDRESS = "http://meetisanapp.azurewebsites.net";
	public static final int SERVER_PORT = 80;

	public static final String KEY_STATUS = "Status";
	public static final String KEY_STATUS_CODE = "StatusCode";
	public static final String KEY_MSG = "Msg";
	public static final String KEY_DATA = "Data";
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_FAILED = 1;
	private static final String API_TUSER = "/api/tuser/";

	// URL
	public static final String FULL_URL_LOGIN = SERVER_ADDRESS + API_TUSER + "Login";
	public static final String FULL_URL_SEND_CODE = SERVER_ADDRESS + API_TUSER + "SendActivationCode";
	public static final String FULL_URL_CHECK_CODE = SERVER_ADDRESS + API_TUSER + "CheckActivationCode";

	// Keys
	public static final String KEY_EMAIL = "Email";
	public static final String KEY_PASSWORD = "Password";
}
