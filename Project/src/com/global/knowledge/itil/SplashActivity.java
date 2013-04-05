package com.global.knowledge.itil;


import com.flurry.android.FlurryAgent;
import com.global.knowledge.itil.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Log;
import android.util.Base64;
import android.content.pm.PackageManager.NameNotFoundException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
     // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.global.knowledge", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    
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