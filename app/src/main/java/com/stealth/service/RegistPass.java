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
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

public class RegistPass extends Activity
{
    EditText et_pass1 ;
	EditText et_pass2 ;
	ProgressDialog progress = null;
    int regist_result = -1 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;
		Const.registPass = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.regist_pass);

		et_pass1 = (EditText)findViewById(R.id.et_pass1);
		et_pass2 = (EditText)findViewById(R.id.et_pass2);
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

    public void next_pressed(View v)
    {
		String str1 = et_pass1.getText().toString().trim().toUpperCase() ;
		String str2 = et_pass2.getText().toString().trim().toUpperCase() ;
		if (!Util.isAlphaNumeric(str1))
        {
            Toast.makeText(this, strR(R.string.password_char_err), Toast.LENGTH_SHORT).show();
            et_pass1.requestFocus();
            return ;
        }
        if ((str1.length() < 4) || (str1.length() > 15))
        {
            Toast.makeText(this, strR(R.string.password_length_err), Toast.LENGTH_SHORT).show();
            et_pass1.requestFocus();
            return ;
        }
        if (!str1.equals(str2))
        {
            Toast.makeText(this, strR(R.string.password_different), Toast.LENGTH_SHORT).show();
            et_pass1.setText("");
            et_pass2.setText("");
            et_pass1.requestFocus();
            return ;
        }
        //Const.savePassword(str1);
        new RegUserTask().execute(str1) ;
    }

    private String strR(int nid)
    {
        return (getResources().getString(nid)) ;
    }

    private class RegUserTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                int n = 0;
                //String pass = Const.getPassword() ;
                n = ReqParser.reqRegUser(strData[0]) ;
                return 0;
            } catch (Exception e) {
                return 99;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
                dspRegResult(result);
        }
    }

    private void dspRegResult(int result)
    {
        //
        // result = 0 if success
        String msg ;
        regist_result = result ;

        switch (result)
        {
            case 0 :
                msg = getResources().getString(R.string.reg_user_success);
                break ;
            default: msg = getResources().getString(R.string.reg_user_fail);
        }

        new AlertDialog.Builder(this).setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (regist_result == 0)
                            finish() ;
                    }
                })
                .show();
    }


}
