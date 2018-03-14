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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

public class RegistEtc extends Activity
{
    EditText et_birth_year ;
    RadioButton rbtn_1 ;
    RadioButton rbtn_2 ;

	ProgressDialog progress = null;
    int sexy = 0 ;
    int birthYear = 1900 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;
		Const.registEtc = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.regist_etc);

		et_birth_year = (EditText)findViewById(R.id.et_birth_year);
        rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1) ;
        rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2) ;

        refresh() ;
	}

    private void refresh()
    {
        rbtn_1.setChecked(false);
        rbtn_2.setChecked(false);
        if (sexy == 1)
            rbtn_1.setChecked(true);
        else if (sexy == 2)
            rbtn_2.setChecked(true);
    }
	
    @Override
    protected void onResume() 
    {
        super.onResume();
    }

    public void rbtn_1_pressed(View v)
    {
        sexy = 1 ;
        refresh();
    }

    public void rbtn_2_pressed(View v)
    {
        sexy = 2 ;
        refresh();
    }

    @Override
	protected void onDestroy()
	{
		Const.registEtc = null ;
		super.onDestroy();
	}

    public void finish_pressed(View v)
    {
		String str1 = et_birth_year.getText().toString().trim() ;
        if (str1.equals(""))
            return ;

        int byear = Integer.parseInt(str1);

        int curYear = Util.getCurYear() ;

        if (byear < 1900 || byear > curYear-8) {
            et_birth_year.setText("");
            et_birth_year.requestFocus();
            return;
        }

        birthYear = byear ;

        if (sexy == 0)
            return ;

        reqEtc() ;
    }

    public void reqEtc()
    {
        new ReqRegEtc().execute() ;
    }

    private class ReqRegEtc extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                int n = 0;
                n = ReqParser.reqRegEtc(Const.targetName, birthYear, sexy);
                return n;
            } catch (Exception e) {
                return 99;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                Const.needGotoPuzzle = true ;
                finish() ;
                return;
            }
        }
    }


	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}
	
}
