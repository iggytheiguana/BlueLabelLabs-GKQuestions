package com.global.knowledge;


import com.flurry.android.FlurryAgent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    
    }
    public void callQuestion(View view) {
    	startActivity(new Intent(getApplicationContext(),QuestionActivity.class));
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
}