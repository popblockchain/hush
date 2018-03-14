/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stealth.service;

import com.stealth.util.ReqParser;
import com.stealth.util.Util;
import com.stealth.hushkbd.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Displays the IME preferences inside the input method setting.
 Intent i=new Intent(android.content.Intent.ACTION_SEND);
 i.setType("text/plain");
 //		String msg2 = String.format(getResources().getString(R.string.share_message2), "http://play.google.com/store/apps/details?id=com.ujsol.hanbangesms");

 i.putExtra(android.content.Intent.EXTRA_TEXT, result_str);
 startActivity(Intent.createChooser(i, "share via"));
 */
public class PopupEncrypt extends Activity 
{
    /*
    android:id="@+id/tv_original"   android:id="@+id/textContent1"   android:id="@+id/btn_copy_orig"
    android:src="@drawable/arrow_down" />    android:id="@+id/tv_level"  android:id="@+id/tv_encrypted"
    android:id="@+id/textContent2"   android:id="@+id/btn_copy_encrypt"   android:id="@+id/btn_share_encrypt"
    android:id="@+id/tv_help" */

    String encryptedStr = "" ;
    String orgStr = "" ;
    TextView tv_original, tv_encrypted, tv_help;
    Button btn_copy_orig, btn_copy_encrypt, btn_share_encrypt ;
    Button btn_group_regist ;
    TextView  textContent2 ;
    EditText etContent1;

    //ysk20161031
    RadioButton rbtn_1 ;
    RadioButton rbtn_2 ;

    Button btn_encrypt ;
    ImageView iv_arrow_down ;
//    String voiceStr = "" ;

    final int REGIST_RESULT = 20;
    final int SELECT_RESULT = 30 ;
    final int PASSWORD_RESULT = 50 ;
    final int VOICE_INPUT_RESULT = 70 ;

    Animation flowAnim ;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        int level = Const.levelEncryptedText(Const.curClip) ;
        if (level < -1)
           orgStr = Const.curClip ;
        else
            orgStr = "" ;

        Const.startClipboardMonitor(this);

        Const.popupEncrypt = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


/*        callReceiver = new CallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(callReceiver, filter); 
 */

		setContentView(R.layout.popup_encrypt);

        btn_group_regist = (Button)findViewById(R.id.btn_group_regist);
        btn_copy_orig = (Button)findViewById(R.id.btn_copy_orig);
        btn_copy_encrypt = (Button)findViewById(R.id.btn_copy_encrypt);
        btn_share_encrypt = (Button)findViewById(R.id.btn_share_encrypt);
        tv_help = (TextView)findViewById(R.id.tv_help);
        tv_original = (TextView)findViewById(R.id.tv_original);
        tv_encrypted = (TextView)findViewById(R.id.tv_encrypted);
        etContent1 = (EditText)findViewById(R.id.etContent1);
        textContent2 = (TextView)findViewById(R.id.textContent2);
        etContent1.setText(orgStr);
        etContent1.setSelection(orgStr.length());
        btn_encrypt = (Button)findViewById(R.id.btn_encrypt);
        iv_arrow_down = (ImageView)findViewById(R.id.iv_arrow_down);

        //ysk20161031
        rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1) ;
        rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2) ;

        flowAnim = AnimationUtils.loadAnimation(this, R.anim.flow) ;

        dspBtns(true) ;

        if (Const.isLock())
            askUnlock();
        else
        {
            doJob() ;
            dspPopup();
        }
    }

    //ysk20161031
    public void rbtn_1_pressed(View v)
    {
        if (Const.getEncryptMethod() == Const.MethodSave)
        {
            Const.saveEncryptMethod(Const.MethodSend);
            doJob();
            dspBtns(true);
        }
    }

    public void rbtn_2_pressed(View v)
    {
        if (Const.getEncryptMethod() == Const.MethodSend)
        {
            Const.saveEncryptMethod(Const.MethodSave);
            doJob();
            dspBtns(true);
        }
    }

    private void doJob()
    {
        doEncrypt();

        etContent1.addTextChangedListener(new TextWatcher() {
                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before,
                                                                        int count) {
                                                  // TODO Auto-generated method stub
                                              }

                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count,
                                                                            int after) {
                                                  // TODO Auto-generated method stub
                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {
                                                  // TODO Auto-generated method stub
                                                  String str = etContent1.getText().toString().trim();
                                                  if (!str.equals(orgStr)) {
                                                      orgStr = str;
                                                      encryptedStr = "";
                                                      dspBtns(false);
                                                  }
                                              }
                                          }
        );        //textContent.setMovementMethod(new ScrollingMovementMethod());
    }


    private void askUnlock()
    {
        // input pass word and unlock
        Intent intent = new Intent(this, PasswordAsk.class);

        startActivityForResult(intent, PASSWORD_RESULT);
    }

    @Override
    protected void onResume() 
    {
        super.onResume();
        // check if group invitation exist
        //Const.checkGroupInvitation(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (!Const.isLock())
            Const.saveLastUsedTime();
    }

    @Override
	protected void onDestroy( )
	{
        Const.popupEncrypt = null ;
    	Const.MyLog("PopupEncrypt onDestroy");
        if (!Const.isLock())
            Const.saveLastUsedTime();
		super.onDestroy() ;
	}


	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) 
	{
	  switch (reqCode)
	    {
	    case REGIST_RESULT :
	    	dspBtns(true);
	        break ;

	    case SELECT_RESULT :
            startEncrypt() ;
	        break ;

            case PASSWORD_RESULT :
                if (resultCode == RESULT_OK)
                {
                    doJob();
                    dspPopup();
                }
                break ;
        }
	}

    protected void gotoVoiceInput() {
        Intent intent = new Intent(this, VoiceInput.class);

        startActivityForResult(intent, VOICE_INPUT_RESULT);
    }



    private void dspBtns(boolean bFirst)
    {
        //ysk20161031
        textContent2.setText(encryptedStr);
        rbtn_1.setText(R.string.encrypt_for_send);
        rbtn_2.setText(R.string.encrypt_for_save);

        if (bFirst)
        {
            iv_arrow_down.setVisibility(View.VISIBLE);
            btn_encrypt.setVisibility(View.INVISIBLE);
        }
        else
        {
            iv_arrow_down.setVisibility(View.INVISIBLE);
            btn_encrypt.setVisibility(View.VISIBLE);
        }

        if (Const.getEncryptMethod() == Const.MethodSend)
        {
            tv_help.setText(getResources().getString(R.string.encrypt_help1));

            tv_encrypted.setText(getResources().getString(R.string.text_encrypted_send));
            rbtn_1.setChecked(true);
            rbtn_2.setChecked(false);
        }
        else
        {
            tv_help.setText(getResources().getString(R.string.encrypt_help2));

            tv_encrypted.setText(getResources().getString(R.string.text_encrypted_save));
            rbtn_1.setChecked(false);
            rbtn_2.setChecked(true);
        }
        flowAnim.setAnimationListener(new FlowAnimationListner());
        tv_help.startAnimation(flowAnim);
    }

    private final class FlowAnimationListner implements Animation.AnimationListener{
        public void onAnimationEnd(Animation animation) {

        }
        public void onAnimationRepeat(Animation animation) {

        }
        public void onAnimationStart(Animation animation) {

        }
    }
 
    public void btn_group_regist_pressed(View view)
    {
        if (!Const.checkRegGroup(this))
            return ;

    	Const.groupRegMode = Const.GRM_Regist ;
        Intent intent = new Intent(PopupEncrypt.this, GroupReg.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);        
        startActivityForResult(intent, REGIST_RESULT);
    }

    /*
    public void btn_select_pressed(View view)
    {
        orgStr = etContent1.getText().toString().trim() ;
        etContent1.setText(orgStr);

        Intent intent = new Intent(PopupEncrypt.this, GroupSelect.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("FromCopy", true);
        startActivityForResult(intent, SELECT_RESULT);
    }
    */

    public void btn_encrypt_pressed(View v)
    {
        doEncrypt();
        orgStr = etContent1.getText().toString().trim();
        dspBtns(true);
    }

    private void startEncrypt() {
            new PrepEncryptTask().execute();
    }


    public void btn_voice_input_pressed(View v)
    {
        gotoVoiceInput();
    }


    public void btn_copy_orig_pressed(View v)
    {
        orgStr = etContent1.getText().toString().trim() ;
        etContent1.setText(orgStr);
        Const.setClipText(orgStr);
        Const.copyTime = System.currentTimeMillis();
        finish() ;
    }

    public void btn_copy_encrypt_pressed(View v)
    {
        Const.setClipText(encryptedStr);
        Const.copyTime = System.currentTimeMillis();
        finish() ;
    }

    private void doEncrypt()
    {
        	// encrypt text and copy to clipboard
        orgStr = etContent1.getText().toString().trim() ;
        etContent1.setText(orgStr);
       	encryptedStr = Const.encrypt(orgStr);
        textContent2.setText(encryptedStr);
    }

    private void shareText(String text)
    {
        Intent i=new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");

        i.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(i, "share via"));
    }

    public void btn_share_encrypt_pressed(View v) {
        shareText(encryptedStr) ;
    }

    public void btn_share_orig_pressed(View v) {
        shareText(etContent1.getText().toString()) ;
    }
    private class PrepEncryptTask extends AsyncTask<String, Integer, Integer>
    {
        ProgressDialog progress;

        @Override
        protected Integer doInBackground(String... strData)
        {
            Const.errCode = 0 ;
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if (progress.isShowing())
            {
                try { progress.dismiss(); } catch(Exception e) { e.printStackTrace(); }
            }

            dspBtns(false);

        }


        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(PopupEncrypt.this);
            progress.setMessage(getString(R.string.encrypt_preparing));
            progress.setCancelable(true);
            progress.show();

            super.onPreExecute();
        }
    }


    private void clickCancelButton()
	{
//		Const.saveLastClip("");
		finish();
	}

	public void ad1_pressed(View v)
	{
	Uri uri = Uri.parse("http://m.naver.com");
	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	startActivity(intent);
	}

	private void gotoSettings()
	{
	Intent intent = new Intent(this, EnvSettings.class);
    
	startActivity(intent);
	}

	public void btn_settings_pressed(View v) 
	{
        // Do something in response to the click
        // Do something in response to the click
		gotoSettings(); 
    }

    //ysk20161031
    private void dspPopup()
    {
        if (Const.getEncryptMethod() != Const.MethodSend)
            dspPopupSave();
    }


    private void dspPopupSave()
    {
        if (!Const.canDspEncryptPopSave())
            return ;

        String str1 = getResources().getString(R.string.encrypt_pop_save_cont) ;

        new AlertDialog.Builder(this)
                 .setMessage(str1)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .setNegativeButton(R.string.dsp_no_more, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Const.saveDspEncryptPopSave(false);
                    }
                })
                .show();
    }
}

 