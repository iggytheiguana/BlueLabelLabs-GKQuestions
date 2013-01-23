package com.global.knowledge;

import com.flurry.android.FlurryAgent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.global.knowledge.twitter.TwitterApp;
import com.global.knowledge.twitter.TwitterApp.TwDialogListener;
import com.global.knowledge.utils.OtherUtils;

public class ResultActivity extends Activity implements Constant{

	public int count;
	TextView textScore,textPer;
	Button butCongra,butPMP;
	private Facebook mFacebook;
	private TwitterApp mTwitter;
	private OtherUtils otherUtils;
	public boolean isConnection = false,DEBUG = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.result);
		otherUtils = new OtherUtils();

		mFacebook = new Facebook(APP_ID);
		mTwitter 	= new TwitterApp(this,twitter_consumer_key,twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);
		initControl();
	}
	
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Constant.FLURRY_ID);
		
	}
	
	protected void onStop()
	{
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	public void initControl() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			count = bundle.getInt("COUNT");
		}
		butCongra = (Button)findViewById(R.id.butCongra);
		
		butPMP = (Button) findViewById(R.id.butPMP);
		
		textScore = (TextView)findViewById(R.id.textScore);
		textScore.append(count+"/15");
		
		int per = Math.round(count *100/15);
		if(DEBUG) Log.w("PER  :   ", ""+per);
		textPer = (TextView)findViewById(R.id.textPer);
		textPer.append(per+"%");
		
		if ((int)per >= 80)
		{
			butCongra.setVisibility(0);
		}
		else
		{
			butCongra.setVisibility(4);
		}
		
//		if((int)per == 100)
//		{
//			butCongra.setVisibility(0);
//		}
		
//		if((int)per <  80)
//		butPMP.setVisibility(4);
	}

	public void callFaceBook(View view) {
		checkInternetConnection();
		if(!isConnection){
			if(DEBUG) Log.d("YES", "OK");
		if (!mFacebook.isSessionValid()) {
			Boolean isWall = true;
			loginAndPostToWall(isWall);
		} else {
			callFaceBookPopup();
		}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	private void loginAndPostToWall(Boolean isWall) {
		mFacebook.authorize(this, PERMISSIONS, new LoginDialogListener(isWall));
	}
	public void callFaceBookPopup() {
		String msg = "I just scored "+count+ "/15 on Global Knowledge's CCNA Practice Quiz. http://globalknowledge.com/ccna-app"; 
		Bundle params = new Bundle();
		params.putString("message",msg);
		params.putString("description", "test test test");
		AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(mFacebook);

		mAsyncRunner.request("me/feed", params, "POST",
				new SampleUploadListener(), null);
		Toast.makeText(getApplicationContext(), "Text Post on Facebook",
				Toast.LENGTH_SHORT).show();
	}

	public void callTwitter(View view) {
		checkInternetConnection();
		if(!isConnection){
		if (mTwitter.hasAccessToken()) {
			if(DEBUG) Log.d("YES", "OK");
			String msg = "I just scored "+count+ "/15 on Global Knowledge's CCNA Practice Quiz. http://globalknowledge.com/ccna-app"; 
			postToTwitter(msg);
		} else {
			if(DEBUG) Log.d("NO", "OK");
			mTwitter.authorize();
		}
		}
	}

	private void alertBox(String message) {

		new AlertDialog.Builder(this)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}
	private void postToTwitter(final String review) {
		new Thread() {
			public void run() {
				int what = 0;
				try {
					mTwitter.updateStatus(review);
				} catch (Exception e) {
					what = 1;
				}
				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	public Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			String text = (msg.what == 0) ? "Posted to Twitter"
					: "Post to Twitter failed";
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();
		}
	};
	
	public void callNewTry(View view) {
		startActivity(new Intent(getApplicationContext(),QuestionActivity.class));
		finish();
	}

	public void callStartOver(View view) {
		Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public void callPMP(View view) {
		Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
		startActivity(myIntent);
	}
	

	class LoginDialogListener implements DialogListener {
		boolean isWall = false;

		public void onComplete(Bundle values) {
			if (isWall) {
				callFaceBookPopup();
			} else {
			}
		}

		public LoginDialogListener(boolean isWall) {
			this.isWall = isWall;
		}

		public void onFacebookError(FacebookError error) {
			Log.e("Facebook Error", error.toString());
		}

		public void onError(DialogError error) {
			Log.e("Facebook Error", error.toString());
		}

		public void onCancel() {
		}

	}
	
	public abstract class BaseRequestListener implements RequestListener {

		public void onFacebookError(FacebookError e, final Object state) {
			e.printStackTrace();
		}

		public void onFileNotFoundException(FileNotFoundException e,
				final Object state) {
			e.printStackTrace();
		}

		public void onIOException(IOException e, final Object state) {
			e.printStackTrace();
		}

		public void onMalformedURLException(MalformedURLException e,
				final Object state) {
			e.printStackTrace();
		}

	}

	public void checkInternetConnection() {
		isConnection  = false;
		if (!otherUtils.isNetworkAvailable(getApplicationContext())) {
			alertBox("No Internet Connection");
			isConnection = true;
			return;
		}
	}
	

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		public void onComplete(String value) {
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;
			postToTwitter("I just scored "+count+ "/15 on Global Knowledge's CCNA Practice Quiz. http://globalknowledge.com/ccna-app");
			Toast.makeText(getApplicationContext(),
					"Connected to Twitter as " + username, Toast.LENGTH_LONG)
					.show();
		}

		public void onError(String value) {
			Toast.makeText(getApplicationContext(),
					"Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
	
	public class SampleUploadListener extends BaseRequestListener {

		@SuppressWarnings("unused")
		public void onComplete(final String response, final Object state) {
			try {
				org.json.JSONObject json = Util.parseJson(response);
				final String src = json.getString("src");
				Log.d("Facebook",src);
				
			} catch (FacebookError e) {
				Log.e("Facebook Error", e.toString());
			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Facebook Error", e.toString());
			}
		}
	}
}