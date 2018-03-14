package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

public class ChangePass extends Activity
{
    TextView tv_change_pass;
    TextView tv_change_pass_help ;
    EditText et_pass_old ;
	EditText et_pass_new ;
    EditText et_pass_again ;
    int change_pass_result = 0 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.change_pass);

		et_pass_old = (EditText)findViewById(R.id.et_pass_old);
        et_pass_new = (EditText)findViewById(R.id.et_pass_new);
        et_pass_again = (EditText)findViewById(R.id.et_pass_again);
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

    public void next_pressed(View v)
    {
        String passOld, passNew, passAgain ;

        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_pass_old.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_pass_new.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_pass_again.getWindowToken(), 0);

        passOld = et_pass_old.getText().toString().trim().toUpperCase();
        passNew = et_pass_new.getText().toString().trim().toUpperCase();
        passAgain = et_pass_again.getText().toString().trim().toUpperCase();

        if (!passOld.equals(Const.getPassword()))
        {
            et_pass_old.setText("");
            et_pass_old.requestFocus();
            return ;
        }

        if ((passNew.length() < 4) || (passNew.length() > 15))
        {
            Toast.makeText(this, strR(R.string.password_length_err), Toast.LENGTH_SHORT).show();
            et_pass_new.requestFocus();
            return ;
        }
        else if (!Util.isAlphaNumeric(passNew))
        {
            et_pass_new.setText("");
            et_pass_new.requestFocus();
            return ;
        }
        else if (!passNew.equals(passAgain)) {
            et_pass_new.setText("");
            et_pass_again.setText("");
            Toast.makeText(this, strR(R.string.change_pass_different), Toast.LENGTH_SHORT).show();
            et_pass_new.requestFocus();
            return ;
        }


        //Const.savePassword(strPass);
        new ReqChangePassTask().execute(passNew) ;
    }

    private String strR(int nid)
    {
        return (getResources().getString(nid)) ;
    }

    private class ReqChangePassTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                int n = 0;
                //String pass = Const.getPassword() ;
                n = ReqParser.reqChangePassword(strData[0]) ;
                return n;
            } catch (Exception e) {
                return 99;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
                dspResult(result);
        }
    }

    private void dspResult(int result)
    {
        //
        // result = 0 if success
        String msg ;
        change_pass_result = result ;

        switch (result)
        {
            case 0 :
                msg = getResources().getString(R.string.change_pass_success);
                break ;
            default: msg = getResources().getString(R.string.change_pass_fail);
        }

        new AlertDialog.Builder(this).setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (change_pass_result == 0)
                            finish() ;
                    }
                })
                .show();
    }


}
