package com.global.knowledge;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.global.knowledge.Database.DBAdapter;
import com.sbstrm.appirater.Appirater;

public class QuestionActivity extends Activity implements Constant,
		OnClickListener {

	private static final boolean DEBUG = false;
	DBAdapter dbAdapter;
	public List<Integer> numberList = new ArrayList<Integer>();
	public TextView textQuestion, textTitle, textExplan;
	public RadioGroup radioGroupQuestion;
	public RadioButton radioButton[] = new RadioButton[5];
	public int currentVal = 0;
	public boolean isQues = false;
	public Vector<Hashtable<String, Object>> mainHashMaps;
	private String explanation;
	private String correctAnswer;
	public int correctAnsCount = 0;
	public Button butSubmit, butRestart;
	public boolean isAnswerClick = false;
	public ScrollView layoutScroll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		
		Appirater.appLaunched(this);

		if(DEBUG) Log.d("ONCREATE  :  ", "OK");
		mainHashMaps = new Vector<Hashtable<String, Object>>();
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
		butSubmit = (Button) findViewById(R.id.butSubmit);
		butRestart = (Button) findViewById(R.id.butRestart);

		textTitle = (TextView) findViewById(R.id.textTitle);
		textQuestion = (TextView) findViewById(R.id.textQuestion);
		textExplan = (TextView) findViewById(R.id.textExplan);

		radioGroupQuestion = (RadioGroup) findViewById(R.id.radioGroupQuestion);
		radioButton[0] = (RadioButton) findViewById(R.id.radio1);
		radioButton[0].setTag(0);
		radioButton[0].setOnClickListener(this);

		radioButton[1] = (RadioButton) findViewById(R.id.radio2);
		radioButton[1].setTag(1);
		radioButton[1].setOnClickListener(this);

		radioButton[2] = (RadioButton) findViewById(R.id.radio3);
		radioButton[2].setTag(2);
		radioButton[2].setOnClickListener(this);

		radioButton[3] = (RadioButton) findViewById(R.id.radio4);
		radioButton[3].setTag(3);
		radioButton[3].setOnClickListener(this);

		radioButton[4] = (RadioButton) findViewById(R.id.radio5);
		radioButton[4].setTag(4);
		radioButton[4].setOnClickListener(this);

		layoutScroll = (ScrollView)findViewById(R.id.layoutScroll);
		
		gererateRandomNumber(133);
		loadDatabase();

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 0, 5f);
			layoutScroll.setLayoutParams(params);
			if(DEBUG) Log.d("YES LAN", "OK");
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
		if(DEBUG) Log.d("onConfigurationChanged", "Port");
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,0, 10f);
		layoutScroll.setLayoutParams(params);
		} else {
			if(DEBUG) Log.d("onConfigurationChanged", "Land");
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,0, 5f);
			layoutScroll.setLayoutParams(params);
		}
	}
	public void gererateRandomNumber(int lenths) {

		numberList.clear();
		Random rng = new Random();
		for (int i = 0; i < 15; i++) {
			while (true) {
				Integer next = rng.nextInt(lenths - 1) + 1;
				if (!numberList.contains(next)) {
					numberList.add(next);
					// if(DEBUG) Log.d("Array", "" + numberList);
					break;
				}
				if(DEBUG) Log.w("Number", "  :  " + numberList);
			}
		}
	}

	public void loadDatabase() {
		isAnswerClick = false;
		openDatabase();
		int number = numberList.get(currentVal);
		String query = "select * from Questions where Number = " + number;
		Cursor cursor;
		cursor = dbAdapter.selectRecordsFromDB(query, null);
		int count = currentVal + 1;

		cursor.moveToFirst();
		textTitle.setText(getStringData(R.string.text_ques) + " " + count + " "
				+ getStringData(R.string.text_ques_1));
		textQuestion.setText(cursor.getString(1).toString());
		textExplan.setVisibility(8);
		currentVal++;
		isQues = true;
		radioButton[0].setText(Html.fromHtml(cursor.getString(2).toString()));
		radioButton[1].setText(Html.fromHtml(cursor.getString(3).toString()));
		radioButton[2].setText(Html.fromHtml(cursor.getString(4).toString()));
		radioButton[3].setText(Html.fromHtml(cursor.getString(5).toString()));
		String element = cursor.getString(6);
		if (element == null || element.length() < 0) {
			radioButton[4].setVisibility(View.GONE);
		} else {
			radioButton[4].setText(element);
		}
		correctAnswer = cursor.getString(7).toString();
		explanation = cursor.getString(8).toString();

		dbAdapter.close();
	}

	public void callNextButton(View view) {
		if (isAnswerClick) {
			if(DEBUG) Log.w("COUNT    :     ", "" + currentVal);
			if (isQues) {
				int nIdRadio = radioGroupQuestion.getCheckedRadioButtonId();
				int mSelectOption = 0;
				if (nIdRadio == R.id.radio1)
					mSelectOption = 0;
				else if (nIdRadio == R.id.radio2)
					mSelectOption = 1;
				else if (nIdRadio == R.id.radio3)
					mSelectOption = 2;
				else if (nIdRadio == R.id.radio4)
					mSelectOption = 3;
				else if (nIdRadio == R.id.radio5)
					mSelectOption = 4;

				int val = 0;
				if (correctAnswer.equals(correctAns[0]))
					val = 0;
				else if (correctAnswer.equals(correctAns[1]))
					val = 1;
				else if (correctAnswer.equals(correctAns[2]))
					val = 2;
				else if (correctAnswer.equals(correctAns[3]))
					val = 3;
				else if (correctAnswer.equals(correctAns[4]))
					val = 4;

				radioButton[val].setTextColor(Color.GREEN);
				if (correctAns[mSelectOption].equals(correctAnswer)) {
					radioButton[mSelectOption].setTextColor(Color.GREEN);
					correctAnsCount++;
				} else {
					radioButton[mSelectOption].setTextColor(Color.RED);
				}
				if(DEBUG) Log.d("nIdRadio  :   UN : ", "" + nIdRadio + "    :    "
						+ mSelectOption);
				isQues = false;

				for (int i = 0; i < 5; i++) {
					radioButton[i].setClickable(false);
				}

				butSubmit.setText(getStringData(R.string.text_next));
				butRestart.setText(getStringData(R.string.text_but_explain));
			} else {
				if (currentVal == 15) {
					if(DEBUG) Log.d("correctAnsCount   :   ", "   " + correctAnsCount);
					Intent intent = new Intent(getApplicationContext(),
							ResultActivity.class);
					intent.putExtra("COUNT", correctAnsCount);
					startActivity(intent);
					finish();
				} else {
					butRestart.setVisibility(0);
					for (int i = 0; i < 5; i++) {
						radioButton[i].setClickable(true);
						radioButton[i].setTextColor(Color.BLACK);
					}
					radioGroupQuestion.clearCheck();
					loadDatabase();
					butSubmit.setText(getStringData(R.string.text_submit));
					butRestart.setText(getStringData(R.string.text_restart));
					layoutScroll.scrollTo(0, 0);
				}
			}
		} else {
			alertBox("Please Select Option ");
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

	public void callRestartButton(View view) {
		if (!isQues) {
			layoutScroll.scrollTo(0, 0);
			textExplan.setVisibility(0);
			textQuestion.setText(explanation);
			butRestart.setVisibility(8);
		} else
			finish();
	}

	public void openDatabase() {
		dbAdapter = DBAdapter.getDBAdapterInstance(this);
		try {
			dbAdapter.createDataBase();
		} catch (IOException e) {
			if(DEBUG) Log.e("Login", e.toString());
		}
		dbAdapter.openDataBase();
	}

	private String getStringData(int id) {
		// TODO Auto-generated method stub
		return getResources().getString(id);
	}

	@Override
	public void onClick(View v) {
		int tag = Integer.valueOf(v.getTag().toString());
		if(DEBUG) Log.d("tag", "" + tag);
		isAnswerClick = true;
	}
}
