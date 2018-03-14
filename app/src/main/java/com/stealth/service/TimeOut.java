package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

public class TimeOut extends Activity
{
	Spinner spinner_time_out = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.time_out);
// Spinner

		spinner_time_out = (Spinner)findViewById(R.id.spinner_time_out);
		ArrayAdapter time_out_Adapter = ArrayAdapter.createFromResource(this,
				R.array.time_out, android.R.layout.simple_spinner_item);
		time_out_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_time_out.setAdapter(time_out_Adapter);

		int index = Const.getIndexFromTimeOut();
		Const.MyLog("index="+index);
		spinner_time_out.setSelection(index);
	}
	
    @Override
    protected void onResume() 
    {
        super.onResume();
    }

	@Override
	protected void onDestroy()
	{
		Const.registPhone = null ;
		super.onDestroy();
	}

    public void ok_pressed(View v)
    {
		int n = spinner_time_out.getSelectedItemPosition();
		long timeOut = Const.getTimeOutFromIndex(n) ;
		Const.saveLockTimeOut(timeOut);
        Const.saveLastUsedTime();

        String text = spinner_time_out.getSelectedItem().toString();
		Const.saveTimeOutText(text);
        String message = String.format(strR(R.string.time_out_set), text) ;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish() ;
	}

	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}
	
}
