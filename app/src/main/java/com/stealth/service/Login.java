package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

public class Login extends Activity
{
    int userIdKind = 0 ; // 0=phone 1=email
    TextView tv_login;
    EditText et_phone ;
	EditText et_pass ;

    int login_result ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.login);

        tv_login = (TextView)findViewById(R.id.tv_login);
		et_phone = (EditText)findViewById(R.id.et_phone);
		et_pass = (EditText)findViewById(R.id.et_pass);

        et_phone.setText(Util.convLocalPhoneNumber(Util.getDeviceLocalPhoneNumber()));

        String str = et_phone.getText().toString().trim() ;
        if (str.length() > 0)
        {
            Util.setEditTextEditable(et_phone,  false);
        }
        else
        {
            userIdKind = 1 ;
            et_phone.setHint(R.string.regist_email_hint);
            et_phone.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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

    public void next_pressed(View v)
    {
        String str1, strPass ;

        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_pass.getWindowToken(), 0);

        if (userIdKind == 0) {
            str1 = et_phone.getText().toString().trim().toUpperCase();
            String str2 = Util.convLocalPhoneNumber(str1);
            Const.saveLocalPhoneNumber(str2);
            Const.targetPhone = Const.getGlobalPhoneNumber();
        }
        else
        {
            str1 = et_phone.getText().toString().trim().toLowerCase();
            if (!Util.checkEmail(str1)) {
                Const.targetPhone = "";
                et_phone.setText("");
                et_phone.requestFocus();
                return;
            }
            else
                Const.targetPhone = str1 ;
        }

        strPass = et_pass.getText().toString().trim().toUpperCase();

        if ((strPass.length() < 4) || (strPass.length() > 15))
        {
            Toast.makeText(this, strR(R.string.password_length_err), Toast.LENGTH_SHORT).show();
            et_pass.requestFocus();
            return ;
        }

        //Const.savePassword(strPass);
        new ReqLoginTask().execute(strPass) ;
    }

    private String strR(int nid)
    {
        return (getResources().getString(nid)) ;
    }

    private class ReqLoginTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                int n = 0;
                //String pass = Const.getPassword() ;
                n = ReqParser.reqUserLogin(strData[0]) ;
                return n;
            } catch (Exception e) {
                return 99;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
                dspLoginResult(result);
        }
    }

    private void dspLoginResult(int result)
    {
        //
        // result = 0 if success
        String msg ;
        login_result = result ;

        switch (result)
        {
            case 0 :
                msg = getResources().getString(R.string.login_user_success);
                break ;
            case 2 :
                msg = getResources().getString(R.string.login_password_err);
                break ;
            default: msg = getResources().getString(R.string.login_user_fail);
        }

        new AlertDialog.Builder(this).setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (login_result == 0)
                            finish() ;
                        else if (login_result == 2)
                        {
                            et_pass.setText("");
                            et_pass.requestFocus();
                        }
                    }
                })
                .show();
    }


}
