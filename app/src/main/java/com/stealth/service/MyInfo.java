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
import android.widget.TextView;
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

public class MyInfo extends Activity
{
    EditText et_my_name ;
    EditText et_birth_year ;
    RadioButton rbtn_1 ;
    RadioButton rbtn_2 ;
    TextView tv_hp ;

	ProgressDialog progress = null;
    int sexy = 0 ;
    int birthYear = 1900 ;
    String myName ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.my_info);

        et_my_name = (EditText)findViewById(R.id.et_my_name) ;
		et_birth_year = (EditText)findViewById(R.id.et_birth_year);
        rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1) ;
        rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2) ;
        tv_hp = (TextView) findViewById(R.id.tv_hp) ;

        new ReqUserCheckTask().execute() ;
    }


    private class ReqUserCheckTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            int n = 0 ;
            try {
                n = ReqParser.reqUserCheck(0);
                if (n != 0)
                return n;
            } catch (Exception e) {
                return 99;
            }
            return n;
        }

        @Override
        protected void onPostExecute(Integer result) {
            refresh() ;
        }
    }

    private void refresh()
    {
        myName = Const.getMyName() ;
        birthYear = Const.getBirthYear() ;
        sexy = Const.getSexy() ;

        et_my_name.setText(myName);

        et_birth_year.setText(birthYear+"");

        tv_hp.setText(strR(R.string.hp_hold)+" = "+Const.getHP());

        dspRBtns();
    }

    private void dspRBtns()
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
        dspRBtns();
    }

    public void rbtn_2_pressed(View v)
    {
        sexy = 2 ;
        dspRBtns();
    }

    @Override
	protected void onDestroy()
	{
		Const.registEtc = null ;
		super.onDestroy();
	}

    public void change_info_pressed(View v)
    {
        String strName = et_my_name.getText().toString().trim() ;
        if (strName.length() < 2) {
            Toast.makeText(this, "Too short, try again!", Toast.LENGTH_LONG);
            Const.targetPhone = "" ;
            et_my_name.requestFocus();
            return;
        }

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

        if (strName.equals(myName) && (birthYear == byear) &&
                (sexy == Const.getSexy())) // no change
        {
            finish() ;
            return ;
        }

        birthYear = byear ;

        if (sexy == 0)
            return ;

        Const.targetName = strName ;
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
                dspSuccess();
            }
        }
    }

    private void dspSuccess()
    {
        String str = strR(R.string.info_change) ;
        String msg = strR(R.string.info_change_success);
        new AlertDialog.Builder(this).setMessage(msg)
                .setTitle(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish() ;
                    }
                })
                .show();
    }

	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}
	
}
