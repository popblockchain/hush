package com.stealth.service;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.Util;

public class PasswordAsk extends Activity {
	public String strR(int id)
	{
		return(getResources().getString(id));
	}

    EditText et_password_ask ;
    TextView tv_password_ask ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.password_ask);

        et_password_ask = (EditText) findViewById(R.id.et_password_ask) ;
        tv_password_ask = (TextView) findViewById(R.id.tv_password_ask) ;

//        setTitle(strR(R.string.password_title));
        dspBtns();
	}
	
    @Override
    protected void onResume()
    {
        super.onResume();
		dspBtns() ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy() ;
    }

	private void dspBtns()
	{
		et_password_ask.setHint(getResources().getString(R.string.password_hint));
        //ysk20161031
        if (Const.curDecryptMethod == Const.MethodSend)
            tv_password_ask.setText(getResources().getString(R.string.password_explain_unlock));
        else
            tv_password_ask.setText(getResources().getString(R.string.password_explain_save));
	}

    private void finishPass() {
        finish();
    }

	public void btn_ok_pressed(View v)
	{
		String sPass = et_password_ask.getText().toString().trim().toUpperCase() ;

        //ysk20161031
        if (Const.curDecryptMethod == Const.MethodSend) {
            if (sPass.equals(Const.getPassword())) {
                Const.saveLock(false);
                Const.saveLastUsedTime();
                setResult(RESULT_OK);
                finishPass();
                return;
            }
        }
        else
        {
            if (sPass.length() >= 4) {
                Const.PasswordSave = sPass ;
                setResult(RESULT_OK);
                finishPass();
                return ;
            }
        }

		if (!Util.isAlphaNumeric(sPass))
		{
			Toast.makeText(this, getResources().getString(R.string.password_char_err), Toast.LENGTH_SHORT);
			et_password_ask.requestFocus() ;
		}
		else if (sPass.length() > 15)
		{
			Toast.makeText(this, "Length too long!", Toast.LENGTH_SHORT);
			et_password_ask.requestFocus() ;
		}
		else
		{
            setResult(99);
            finishPass() ;
		}
	}
}
