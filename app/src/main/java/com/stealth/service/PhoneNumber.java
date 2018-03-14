package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.stealth.crypto.AES256;
import com.stealth.jncryptor.RNC256;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;
import com.stealth.hushkbd.R;

public class PhoneNumber extends Activity
{
    EditText et_phone_number ;
	ProgressDialog progress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;
		Const.phoneNumber = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.phone_number);

		et_phone_number = (EditText)findViewById(R.id.et_phone_number);
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) 
	{
	  switch (reqCode) 
	    {
	    case 100 :
	    	if (Const.curGroupInfo == null)
	    		finish() ;
	        break ;
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
		Const.phoneNumber = null ;
		super.onDestroy();
	}

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
	
	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}
	
	private void askWait()
	{
		String str = strR(R.string.phone_number_sent);

		new AlertDialog.Builder(this).setMessage(str) 
		.setCancelable(false)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {    // ?
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// yes
						progress = new ProgressDialog(PhoneNumber.this);
						progress.setMessage("Waiting...");
						progress.setCancelable(false);
						progress.show();
					}
				})
		.show();

		new CheckMessageTask().execute() ;
    }

	private class CheckMessageTask extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... strData)
		{
			try
			{
				Const.cr = getContentResolver();
				long sTime = Util.getCurTime(); //nTimes = 0 ;
				while (true)
				{
					if (SendSMS.getMessageBox(PhoneNumber.this))
						return 0 ;
					long cTime = Util.getCurTime() ;
					if ((cTime-sTime) >= 120*1000)
						break ;
					else {
						Util.sleep(100);
					}
				}
			}
			catch(Exception e)
			{
				return 99;
			}
			return 1 ;
		}


		@Override
		protected void onPostExecute(Integer result)
		{
			if (progress != null)
				progress.dismiss();

			if (result == 0)
			{
				setResult(RESULT_OK);
				finish() ;
			}
		}
	}

	public void onSendError()
	{
         Toast.makeText(this, "Message Send Error!", Toast.LENGTH_LONG);
		Const.targetPhone = "" ;
		et_phone_number.requestFocus();
	}

	public void onReceiveMessage()
	{
		if (progress != null && progress.isShowing())
		{
			try {
				progress.dismiss();
				progress = null ;
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
}
