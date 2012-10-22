package com.global.knowledge;

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
}