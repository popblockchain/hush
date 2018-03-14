package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.stealth.util.Util;
import com.stealth.hushkbd.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceInput extends Activity
{
	Intent voice_intent;
	SpeechRecognizer recognizer ;
	public RecognitionListener voice_recognition ;
	boolean isStarted = false ;
	TextView tv_speak_now ;
	Timer timer = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.voice_input);

		tv_speak_now = (TextView)findViewById(R.id.tv_speak_now);

		initVoice();
	}

	private void startTimer()
	{
		if (timer != null)
			return ;

		timer = new Timer();

		timer.scheduleAtFixedRate(
				new TimerTask()
				{
					@Override

					public void run()
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								onTimerTick();
							}
						});

						if (timer == null)
						{
							return;
						}

					}
				}, 2000, 2000); // after 0.5 second, 1 seconds interval
	}

	private void stopTimer()
	{
		if (timer != null)
			timer.cancel();
		timer = null ;
	}

	private void onTimerTick() {
		stopTimer();
		initVoice();
		startVoiceInput();
	}

	private void startVoiceInput()
	{
		try
		{
			recognizer.startListening(voice_intent);
		}
		catch(Exception e)
		{
			Const.MyLog("voice inpu err="+e.toString());
			finish();
		}
	}


	private void initVoice() {
		voice_recognition = new RecognitionListener() {
			@Override
			public void onReadyForSpeech(Bundle params) {
				Const.MyLog("onReadyForSpeech");
				tv_speak_now.setText(strR(R.string.speak_now));
			}

			@Override
			public void onBeginningOfSpeech() {
				Const.MyLog("onBeginningOfSpeech");
			}

			@Override
			public void onRmsChanged(float rmsdB) {
				//Const.MyLog("onRmsChanged");
			}

			@Override
			public void onBufferReceived(byte[] buffer) {
				Const.MyLog("onBufferReceived");
			}

			@Override
			public void onEndOfSpeech() {
				Const.MyLog("onEndOfSpeech");
			}

			@Override
			public void onError(int error) {
				Const.MyLog("onError");
				dspErrorAndFinish() ;
			}

			@Override
			public void onResults(Bundle results) {
				String key = "" ;
				String str = "" ;
				key = SpeechRecognizer.RESULTS_RECOGNITION ;
				ArrayList<String> mResults = results.getStringArrayList(key);
				String[] rs = new String[mResults.size()];
				mResults.toArray(rs);
				for (int i=0; i < mResults.size(); i++)
				{
					Const.MyLog("result for i="+i+","+rs[i]);
				}
				str += ""+rs[0] ;
				Const.popupEncrypt.etContent1.getText().insert(Const.popupEncrypt.etContent1.getSelectionStart(), str);
				finish();
			}

			@Override
			public void onPartialResults(Bundle partialResults) {
				Const.MyLog("onPartialResults");

			}

			@Override
			public void onEvent(int eventType, Bundle params) {
				Const.MyLog("onEvent");
			}
		};

		voice_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		voice_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
		String lang_code = Util.getLanguageCode() + "-" + Util.getCountryCode();
		voice_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang_code);
		recognizer = SpeechRecognizer.createSpeechRecognizer(this);

		recognizer.setRecognitionListener(voice_recognition);
	}

	private void closeVoice() {
		tv_speak_now.setText("");
		recognizer.destroy();
	}

	@Override
	public void onDestroy()
	{
		closeVoice();

		super.onDestroy();
	}


	private void dspErrorAndFinish()
	{
		new AlertDialog.Builder(this).setMessage(strR(R.string.voice_input_err))
				.setCancelable(false)
				.setPositiveButton(R.string.voice_input_try_again, new DialogInterface.OnClickListener()
				{	// ?
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// try again
						closeVoice() ;
						startTimer();
					}
				})
				.setNegativeButton(R.string.voice_input_finish, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						finish();
					}
				})
				.show();
	}

	public void btn_cancel_pressed(View v)
	{
		finish();
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data)
	{
	  switch (reqCode)
	    {
	    case 100 :
	    	//dspRbtns();
	        break ;
	    }
	}

    @Override
    protected void onResume()
    {
        super.onResume();
		if (!isStarted) {
			isStarted = true ;
			startTimer();
		}
	}


	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}

}
