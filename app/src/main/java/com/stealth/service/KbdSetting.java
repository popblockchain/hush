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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stealth.util.Util;
import com.stealth.hushkbd.Const1;
import com.stealth.hushkbd.R;

import java.util.Timer;

/**
 * Displays the IME preferences inside the input method setting.
 */
public class KbdSetting extends Activity
{
	Button btnSelect ;
    Button btnDefault ;
    TextView textContent ;
//    TextView tv_title ;
    final int SELECT_RESULT = 2000 ;
    GroupInfo askGroup = null ;
    int       myState = 0 ;
    boolean   bDoneGroupInfos = false ;
    //boolean bCheckedInvite = false ;
	private Timer timer = null ; //ysk20160113

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.kbd_setting);
        btnSelect = (Button)findViewById(R.id.select_keyboard);
        btnDefault = (Button)findViewById(R.id.choose_default);
        textContent = (TextView)findViewById(R.id.textContent);
        //textContent.setMovementMethod(new ScrollingMovementMethod());
        btnSelect.setOnClickListener(new Button.OnClickListener()
        {
        	@Override
        	public void onClick(View v) 
        	{
        	 // TODO Auto-generated method stub
        		selectKeyboard();
        	}
        });
          
        btnDefault.setOnClickListener(new Button.OnClickListener()
        {
        	@Override
        	public void onClick(View v) 
        	{
        	 // TODO Auto-generated method stub
        		Const1.selectDefaultKeyboard(false);
        	}
        });

        textContent = (TextView)findViewById(R.id.textContent);
    }

    //ysk20160209 -------------------------------------------------------
    @Override
    protected void onResume() 
    {
        super.onResume();

		dspScreen() ;
        dspBtns() ;
    }

    //ysk20160209 -----------------------------------------------------

    /*
	public void startTimer()
	{
		if (timer != null)
			return ;
		
	    //Const.MyLog("Main", "startTimer");
		timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask()
        { 
    	   @Override
    	
    	   public void run() 
    	   {
    		    onTimerTick();
    	   }
       }, 0, 500); // 0.5 seconds interval
	}

    private void onTimerTick() 
    {
    	try
    	{
    		if (!bCheckedInvite &&
    			Const.inviteGroup != null)
    		{    	
    			bCheckedInvite = true ;
    			gotoSettings() ;
    			timer.cancel();
    		}
    		//checkInvite();
    	}
    	catch (Exception e)
    	{
    		
    	}
    }
    */
    
    @Override
	protected void onDestroy( )
	{
		super.onDestroy() ;
	}

    private void dspBtns() 
    {
    	if (Const.getService() == null)
    		return ;
    	
    	if (Const1.isStealthSelected())
    		btnSelect.setVisibility(View.INVISIBLE);
    	else
    		btnSelect.setVisibility(View.VISIBLE);
    	
    	if (Const.isStealthDefault())
    		btnDefault.setVisibility(View.INVISIBLE);
    	else
    		btnDefault.setVisibility(View.VISIBLE);
    }
    
    private void dspScreen()
    {
        btnSelect.setText(getResources().getString(R.string.select_keyboard));
        
        btnDefault.setText(getResources().getString(R.string.choose_default));
        
        textContent.setText(getResources().getString(R.string.main_content_kbd));
    }

    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) 
    {
        super.onWindowFocusChanged(hasFocus);
    	dspBtns();
    	dspScreen() ;
    	if ((!Const1.bDspSuccess) && (Const1.isStealthSelected()) && (Const.isStealthDefault()))
    	{
    		Const1.bDspSuccess = true ;
    		dspSuccess() ;
    	}
    }
    
    public String strR(int id)
    {
    	return(getResources().getString(id));
    }
    
    
    private void selectKeyboard()
    {
 		startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags
                (Intent.FLAG_ACTIVITY_CLEAR_TASK), SELECT_RESULT);
    }
    


    public void onBackPressed()
    {
    		super.onBackPressed();
        if (Const.envSettings != null)
            Const.envSettings.refresh();
    }
    
    private void dspSuccess()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(getResources().getString(R.string.finished));
        alert.setCancelable(false);

        DialogInterface.OnClickListener OKListener = new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
            	finish() ;
            }
        };

        alert.setPositiveButton(getResources().getString(R.string.ok), OKListener);
        alert.show();

    }

    
    /*
    private void dspClipText()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (Const.getDspPopup() == Const.DspPureText)
            alert.setTitle("Encrypt this text?");
        else
            alert.setTitle("Decrypted Text");
        alert.setMessage(Const.sClip);
        alert.setCancelable(false);

        DialogInterface.OnClickListener OKListener = new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
                clickOkButton() ;            	
            }
        };

        DialogInterface.OnClickListener CancelListener = new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
                // Do nothing; cancel...
            	clickCancelButton() ;
            } 
        };

        if (Const.getDspPopup() == Const.DspPureText)
            alert.setPositiveButton("Encrypt & Copy", OKListener);
        else
            alert.setPositiveButton("Copy", OKListener);
        	
        alert.setNegativeButton("Close", CancelListener);
        alert.show();

    }
    
	
	
    private void clickOkButton()             	
    {
        if (Const.getDspPopup() == Const.DspPureText)
        {
        	// encrypt text and copy to clipboard
        	Const.sClip = Const.encrypt(Const.DefaultKey, Const.sClip);
//        	Const.saveLastClip(Const.sClip);
        	Const.setClipText(Const.sClip);
        }
        else
        {
        	// copy to clip board
//        	Const.saveLastClip(Const.sClip);
        	Const.setClipText(Const.sClip);
        }
         MainActivity.this.finish();
         Const.getDspPopup() = 0 ;
         Const.copyTime = System.currentTimeMillis();

    }
    
	private void clickCancelButton() 
	{
//		Const.saveLastClip("");
	    Const.getDspPopup() = 0 ;
		MainActivity.this.finish();
	}

*/
    
}

 