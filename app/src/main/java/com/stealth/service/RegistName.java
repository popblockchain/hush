package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

public class RegistName extends Activity
{
    EditText et_regist_name ;
	ProgressDialog progress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;
		Const.registName = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.regist_name);

		et_regist_name = (EditText)findViewById(R.id.et_regist_name);

		et_regist_name.setText(Const.getMyName());

        String str = et_regist_name.getText().toString().trim() ;
	}
	
    @Override
    protected void onResume() 
    {
        super.onResume();
        if (Const.needGotoPuzzle)
            finish();
    }

	@Override
	protected void onDestroy()
	{
		Const.registName = null ;
		super.onDestroy();
	}

    public void next_pressed(View v)
    {
		String str1 = et_regist_name.getText().toString().trim() ;
        if (str1.length() < 2) {
            Toast.makeText(this, "Too short, try again!", Toast.LENGTH_LONG);
            Const.targetPhone = "" ;
            et_regist_name.requestFocus();
            return;
        }
        Const.targetName = str1 ;
        gotoRegistEtc() ;
    }

    private void gotoRegistEtc()
    {
        Intent intent = new Intent(this, RegistEtc.class);
        startActivity(intent);
    }

	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}
	
}
