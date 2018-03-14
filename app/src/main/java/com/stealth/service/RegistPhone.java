package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stealth.crypto.AES256;
import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

import org.w3c.dom.Text;

public class RegistPhone extends Activity
{
    TextView tv_regist_phone ;
    TextView tv_regist_phone_help ;
    EditText et_phone_number ;
	ProgressDialog progress = null;
    int UserIdKind = 0 ; // 0=phone, 1=email

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;
		Const.registPhone = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.regist_phone);

        tv_regist_phone = (TextView) findViewById(R.id.tv_regist_phone);
        tv_regist_phone_help = (TextView) findViewById(R.id.tv_regist_phone_help);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);

        if (Util.getCountryCode().equals("KR")) // if korea telephone id can be allowed
        {
            et_phone_number.setText(Util.convLocalPhoneNumber(Util.getDeviceLocalPhoneNumber()));
        }

        String str = et_phone_number.getText().toString().trim() ;
        if (str.length() > 0)
        {
            Util.setEditTextEditable(et_phone_number,  false);
        }
        else
        {
            UserIdKind = 1 ;
            tv_regist_phone.setText(R.string.regist_email);
            et_phone_number.setHint(R.string.regist_email_hint);
            tv_regist_phone_help.setText(R.string.regist_email_help);
            et_phone_number.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
		Const.registPhone = null ;
		super.onDestroy();
	}

    public void next_pressed(View v)
    {
		String str1 = et_phone_number.getText().toString().trim() ;

        if (UserIdKind == 0) {
            String str2 = Util.convLocalPhoneNumber(str1);
            Const.saveLocalPhoneNumber(str2);

            String str = Util.convPurePhoneNumber(str2);
            Const.targetPhone = Const.getGlobalPhoneNumber() ;

            if (str.length() < 10) {
                Toast.makeText(this, "Too short, try again!", Toast.LENGTH_LONG);
                Const.targetPhone = "";
                et_phone_number.setText("");
                et_phone_number.requestFocus();
                return;
            }
        }
        else
        {
            Const.targetPhone = str1.toLowerCase();
            if (!Util.checkEmail(str1)) {
                Const.targetPhone = "" ;
                et_phone_number.setText("");
                et_phone_number.requestFocus();
                return;
            }
        }
        reqPhoneCheck();
    }

    public void reqPhoneCheck()
    {
        new ReqPhoneCheckTask().execute() ;
    }

    private class ReqPhoneCheckTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                int n = 0;
                n = ReqParser.reqPhoneCheck();
                return n;
            } catch (Exception e) {
                return 99;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result <= 1) {
                // user can regist
                Const.needGotoRegistPass = true ;
                finish() ;
                return;
            }
            else if (result == 99)
            {
                // 통신 오류
            }
            else
            {
                dspExistUser() ;
            }

                /*
                else if (Const.getMySid() == 0)
                {
                    // need to regist user
                    reqRegUser() ;
                    //gotoJoin() ;
                }
                */
        }
    }

    private void dspExistUser()
    {
        // ok pressed -> Const.needDspLogin = true
        String str = getResources().getString(R.string.user_already) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Const.needGotoLogin = true ;
                        finish() ;
                    }
                })
                .show();
    }
    /*
	public void btn_ok_pressed(View v)
	{
		String str = Util.convPurePhoneNumber(et_phone_number.getText().toString());
		Const.targetPhone = str ;
		if (Const.targetPhone.length() < 10) {
			Toast.makeText(this, "Too short, try again!", Toast.LENGTH_LONG);
			Const.targetPhone = "" ;
			et_phone_number.requestFocus();
			return;
		}

		SendSMS.sendSMS(this, Const.targetPhone, "wordlock<"+ AES256.encrypt(Util.getDeviceNumber(), Const.targetPhone) + ">");
		askWait();
	}
	*/
	
	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}
	
}
