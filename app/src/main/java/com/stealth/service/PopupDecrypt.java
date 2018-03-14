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

import com.stealth.hushkbd.R;
import com.stealth.jncryptor.StreamIntegrityException;
import com.stealth.util.ImageViewSlice;
import com.stealth.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Displays the IME preferences inside the input method setting.
 */
public class PopupDecrypt extends Activity {
    //Button btnCopy = null;
    TextView tv_Title;
    TextView textContent;
    TextView tv_remained;
    TextView tv_puzzle_rcvd;
    final int PASSWORD_RESULT = 40;
    long startPauseTime = 0l;
    ImageViewSlice iv_puzzle_part;
    FrameLayout fl_puzzle_part ;
    int piecesRow;
    int curLevel;
    Bitmap bitmapPuzzle = null;
    String puzzleImageFullPath ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Const.startClipboardMonitor(this);

        Const.popupDecrypt = this;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Const.curClip has original(decrypted text)

/*        callReceiver = new CallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(callReceiver, filter); 
 */
        setContentView(R.layout.popup_decrypt);
        setTitle(R.string.app_name_decrypt);

        //btnCopy = (Button)findViewById(R.id.btn_copy);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        textContent = (TextView) findViewById(R.id.textContent);
        tv_remained = (TextView) findViewById(R.id.tv_remained);
        tv_puzzle_rcvd = (TextView) findViewById(R.id.tv_puzzle_rcvd);
        iv_puzzle_part = (ImageViewSlice) findViewById(R.id.iv_puzzle_part);
        fl_puzzle_part = (FrameLayout) findViewById(R.id.fl_puzzle_part) ;

        curLevel = Const.getMyOrgLevel();
        piecesRow = Const.getPiecesRow(curLevel);

            //ysk20161031
           int level = Const.levelEncryptedText(Const.curClip);
            if (level == -1)
            {
                // encrypted for save
                Const.curDecryptMethod = Const.MethodSave ;
                askSavePassword();//askUnlock();
            }
            else {
                Const.curDecryptMethod = Const.MethodSend ;
                if (Const.isLock())
                    askUnlock();
                else {
                    doJob();
                    dspPopup();
                    preparePuzzle();
                }
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Const.needGotoPuzzle) {
            Const.needGotoPuzzle = false;
            gotoPuzzle(null);
        }
        refresh() ;
    }


    private void preparePuzzle() {
        //ysk20161031
        if (Const.curDecryptMethod == Const.MethodSend)
            new PreparePuzzleTask().execute();
    }

    private class PreparePuzzleTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                puzzleImageFullPath = Util.getDownloadFullPath(Const.puzzleImagePath, curLevel+".png") ;

                if (!Util.existFile(puzzleImageFullPath))
                    bitmapPuzzle = Util.downloadUrlImage(Const.getYoutubeImageUrl(curLevel));
                else
                    bitmapPuzzle = Util.getHushBitmap(puzzleImageFullPath);

            } catch (Exception e) {
                return 99;
            }
            return 0 ;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0)
            {
                refresh();
                if (!Util.existFile(puzzleImageFullPath))
                {
                    Util.saveBitmapToPngFile(bitmapPuzzle, Const.puzzleImagePath, curLevel+".png") ;
                }

            }
        }
    }


    private void doJob()
    {
        Const.curClip=Const.decrypt(Const.curClip);
        textContent.setText(Const.curClip);
    }

    private void askUnlock()
    {
        // input pass word and unlock
        Intent intent = new Intent(this, PasswordAsk.class);

        startActivityForResult(intent, PASSWORD_RESULT);
    }

    //ysk20161031
    private void askSavePassword()
    {
        Intent intent = new Intent(this, PasswordAsk.class);

        startActivityForResult(intent, PASSWORD_RESULT);
    }

    @Override
    public void onBackPressed()
    {
        //if (Const.checkPauseTime())
           super.onBackPressed();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (!Const.isLock())
            Const.saveLastUsedTime();
    }


    @Override
	protected void onDestroy() {
        Const.popupDecrypt = null ;
        Const.MyLog("PopupDecrypt onDestroy");
        if (!Const.isLock())
            Const.saveLastUsedTime();
		super.onDestroy() ;
	}


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        switch (reqCode)
        {
            case PASSWORD_RESULT :
                if (resultCode == RESULT_OK)
                {
                    doJob();
                    dspPopup();
                    preparePuzzle() ;
                }
                break ;
        }
    }

    private void refresh()
    {
        String str = "" ;
        dspRemained() ;
        dspTitleContent();

        //ysk20161031
        if (Const.curDecryptMethod == Const.MethodSave ||
                (Const.getPuzzleNum() < 0) || //ysk20161114
                Const.senderSid == Const.getMySid() || Const.errCode == Const.EC_PASSED_24HOURS) {
            tv_puzzle_rcvd.setVisibility(View.INVISIBLE);
            fl_puzzle_part.setVisibility(View.INVISIBLE);
        }
        else
        {
            if (Const.getPuzzleNum() >= 0) {
                int puzzleNum = Const.getPuzzleNum() % (piecesRow * piecesRow);
                iv_puzzle_part.setSlices(piecesRow, piecesRow);
                iv_puzzle_part.setTargetSlice(puzzleNum / piecesRow, puzzleNum % piecesRow);
                iv_puzzle_part.setImgResource(bitmapPuzzle);
            }
        }
    }

    public String strR(int id)
    {
        return(getResources().getString(id));
    }

    private void dspRemained()
    {
        //ysk20161031
        if (Const.curDecryptMethod == Const.MethodSave)
        {
            tv_remained.setText(strR(R.string.decrypt_help_save));
        }
        else if ((Const.errCode == Const.EC_PASSED_24HOURS)  || (Const.remainedMinutes <= 0))

        {
            tv_remained.setText(strR(R.string.decrypt_time_out));
        }
        else
        {
            String time1 = getHourMinutes(24*60 - Const.remainedMinutes) ;
            String time2 = getHourMinutes(Const.remainedMinutes) ;

            if (time1.equals(""))
                time1 = strR(R.string.little_while) ;

            String str = String.format(strR(R.string.decrypt_hours_other1), time1) +
                         " " + String.format(strR(R.string.decrypt_hours_other2), time2) ;
            tv_remained.setText(str);
        }
    }

    private String getHourMinutes(int minutes)
    {
        int hours = minutes / 60;
        int mins = minutes % 60;
        String time = "";
        if (hours > 0)
            time = hours + strR(R.string.hours);
        if (mins > 0) {
            if (!time.equals(""))
                time += " ";

            time += mins + strR(R.string.minutes);
        }
        return time ;
    }

    private void dspTitleContent()
    {
        if (Const.errCode > 0)
            tv_Title.setText(getResources().getString(R.string.err_title));
        else
            tv_Title.setText(getResources().getString(R.string.decrypted_text));
    }

    /*
    private class DecryptTask extends AsyncTask<String, Integer, Integer>
    {
        ProgressDialog progress;

        @Override
        protected Integer doInBackground(String... strData)
        {
            Const.errCode = 0 ;
            Const.curClip = Const.decrypt(Const.curClip);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if (progress.isShowing())
            {
                try { progress.dismiss(); } catch(Exception e) { e.printStackTrace(); }
            }

            if (result == 0)
            {
                if (Const.errCode > 0)
                {
                    Const.curClip = "" ;
                    dspTitleContent();
                    dspScreen();
                }
                else {
                    dspTitleContent();
                    dspScreen();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(PopupDecrypt.this);
            progress.setMessage(getString(R.string.decrypt_preparing));
            progress.setCancelable(true);
            progress.show();

            super.onPreExecute();
        }
    }

    */

    /*
    private void clickCopyButton()             	
    {
        	// copy to clip board
//        	Const.saveLastClip(Const.sClip);
        if (Const.checkPauseTime()) {
            Const.setClipText(Const.curClip);
            Const.copyTime = System.currentTimeMillis();
            finish();
        }
    }
    
	private void clickCancelButton() 
	{
//		Const.saveLastClip("");
		finish();
	}
	*/

    public void btn_puzzle_pressed(View v)
    {
        if (Const.getMyName().equals(""))
            askPuzzle();
        else
            gotoPuzzle(null);
    }

    private void askPuzzle()
    {
        String str = getResources().getString(R.string.regist_name_ask);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage(str);
        alert.setCancelable(false);

        DialogInterface.OnClickListener OKListener = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                gotoRegistName();
            }
        };

        alert.setPositiveButton(getResources().getString(R.string.ok), OKListener);
        alert.show();
    }

    private void gotoRegistName()
    {
        Intent intent = new Intent(this, RegistName.class);
        startActivity(intent);
    }


    public void ad1_pressed(View v)
	{
	Uri uri = Uri.parse("http://m.naver.com");
	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	startActivity(intent);
	}

	private void gotoSettings()
	{
        //if (!(Const.checkPauseTime()))
        //    return ;

        Intent intent = new Intent(this, EnvSettings.class);

        startActivity(intent);
	}

	public void btn_settings_pressed(View v) 
	{
        // Do something in response to the click
        // Do something in response to the click
		gotoSettings(); 
    }

    public void gotoPuzzle(View v)
    {
        Const.curLevel = Const.getMyOrgLevel() ;
        Const.prevPuzzleState = false ;
        Intent intent = new Intent(this, Puzzle.class);
        startActivity(intent);
    }

    private void dspPopup()
    {
        //ysk20161114
        if (!Const.canDspDecryptPop())
            return ;
        if (Const.senderSid == Const.getMySid())
            return ;
        if (Const.remainedMinutes == 0)
            return ;

        String str = getResources().getString(R.string.decrypt_pop_title) ;

        String str1 = getResources().getString(R.string.decrypt_pop_cont) ;

        //ysk20161114
        if (Const.getPuzzleNum() < 0)
            str1 = getResources().getString(R.string.puzzle_used) ;


        new AlertDialog.Builder(this).setTitle(str)
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
                        Const.saveDspDecryptPop(false);
                    }
                })
                .show();
    }

}

 