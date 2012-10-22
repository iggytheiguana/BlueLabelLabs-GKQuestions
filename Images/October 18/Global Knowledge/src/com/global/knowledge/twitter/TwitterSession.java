/**
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * http://www.londatiga.net
 */
package com.global.knowledge.twitter;

import twitter4j.http.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TwitterSession {
	private SharedPreferences sharedPref;
	private Editor editor;
	
	private static final String TWEET_AUTH_KEY = "105362610-6TI3oWIlwidsHcrYt5nQSkSJONdpq24GPZLkR1AY";
	private static final String TWEET_AUTH_SECRET_KEY = "Z1mfi7aPZz6Lko09CF0ioOLz67hTOQt4XbIFaLJFupQ";
	/*	private static final String TWEET_AUTH_KEY = "105362610-mVCvXKIxpm6fqIn6uMp6RhJDv5X7BwMEsdQbXXDr";
	private static final String TWEET_AUTH_SECRET_KEY = "kT2kZ4O1IznsUeuviNhDeozAqSQuR7XS7us2cA8AM";
	 * */
	
	private static final String TWEET_USER_NAME = "user_name";
	private static final String SHARED = "Twitter_Preferences";
	
	public TwitterSession(Context context) {
		sharedPref 	  = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		
		editor 		  = sharedPref.edit();
	}
	
	public void storeAccessToken(AccessToken accessToken, String username) {
		editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
		editor.putString(TWEET_USER_NAME, username);
		
		editor.commit();
	}
	
	public void resetAccessToken() {
		editor.putString(TWEET_AUTH_KEY, null);
		editor.putString(TWEET_AUTH_SECRET_KEY, null);
		editor.putString(TWEET_USER_NAME, null);
		
		editor.commit();
	}
	
	public String getUsername() {
		return sharedPref.getString(TWEET_USER_NAME, "");
	}
	
	public AccessToken getAccessToken() {
		String token 		= sharedPref.getString(TWEET_AUTH_KEY, null);
		String tokenSecret 	= sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);
		
		if (token != null && tokenSecret != null) 
			return new AccessToken(token, tokenSecret);
		else
			return null;
	}
}