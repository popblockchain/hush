package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

public class PuzzleReg extends Activity
{
    ImageView iv_puzzle ;
    EditText et_puzzle_url ;
    //ysk20161206
    EditText et_image_url ;
    EditText et_utb_title ;
    Bitmap bitmapPuzzle = null;

    String puzzleUrl = "" ;
    String imageUrl = "" ;
    String utbTitle = "" ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.puzzle_reg);

        et_puzzle_url = (EditText)findViewById(R.id.et_puzzle_url);
        et_image_url = (EditText)findViewById(R.id.et_image_url);
        et_utb_title = (EditText)findViewById(R.id.et_utb_title);
        iv_puzzle = (ImageView)findViewById(R.id.iv_puzzle);

        //ysk20161206
        et_image_url.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){
                    //this if condition is true when edittext lost focus...
                    //check here for number is larger than 10 or not
                    setImageUrl();
                }
            }
        });
	}

    private void setImageUrl()
    {
        String str = et_image_url.getText().toString().trim() ;
        if (str.equals(""))
        {
            String str1 = et_puzzle_url.getText().toString().trim() ;
            if (str1.contains("youtu.be"))
            {
                et_image_url.setText(Const.getYoutubeImageUrl(str1));
            }
        }
    }

    @Override
    protected void onResume() 
    {
        super.onResume();
    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

    public void check_pressed(View v)
    {
        //ysk20161206
//        String str1 = et_puzzle_url.getText().toString().trim() ;
        String img_url = et_image_url.getText().toString().trim() ;

        /*
        if (!str1.startsWith("https://youtu.be/"))
        {
            et_puzzle_url.requestFocus() ;
            return ;
        }
        */

        new CheckYoutubeTask().execute(img_url) ;
    }

    private class CheckYoutubeTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                //String imageUrl = Const.getYoutubeImageUrl(strData[0]) ;
                bitmapPuzzle = Util.downloadUrlImage(strData[0]);
            } catch (Exception e) {
                return 99;
            }
            return 0 ;
        }

        @Override
        protected void onPostExecute(Integer result) {
            iv_puzzle.setImageBitmap(bitmapPuzzle);
        }
    }

    public void cleanFields()
    {
        et_puzzle_url.setText("");
        et_image_url.setText("");
        et_utb_title.setText("");
        iv_puzzle.setImageBitmap(null);
        bitmapPuzzle = null ;
    }

    public void done_pressed(View v)
    {
        if (bitmapPuzzle == null)
        {
            askCheck() ;
            return ;
        }

        puzzleUrl = et_puzzle_url.getText().toString().trim() ;

        //ysk20161206
        imageUrl = et_image_url.getText().toString().trim() ;

        utbTitle = et_utb_title.getText().toString().trim() ;
        if (utbTitle.length() < 4)
        {
            //ysk20161206
            Toast.makeText(this, strR(R.string.utb_title_short), Toast.LENGTH_SHORT).show();
            et_utb_title.requestFocus();
            return;
        }

        new RegYoutubeTask().execute(puzzleUrl, imageUrl, utbTitle) ;
    }

    private class RegYoutubeTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                ReqParser.reqRegYoutube(strData[0], strData[1], strData[2]);
            } catch (Exception e) {
                return 99;
            }
            return 0 ;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0)
            {
                dspRegYoutubeResult(true);
            }
            else {
                dspRegYoutubeResult(false);
            }
        }
    }


    private void dspRegYoutubeResult(boolean bSuccess)
    {
        String str ;
        if (bSuccess)
        {
            String str1 = strR(R.string.puzzle_reg_success);
            str = String.format(str1, Const.registeredLevel) ;
        }
        else
        {
            str = strR(R.string.puzzle_reg_fail);
        }
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        cleanFields() ;
                    }
                })
                .show();
    }

    private void askCheck()
    {
            String str1 = strR(R.string.puzzle_check_first);

        new AlertDialog.Builder(this).setMessage(str1)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }

	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}
	
}
