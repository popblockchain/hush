/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stealth.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.stealth.hushkbd.Const1;
import com.stealth.hushkbd.R;

import java.util.Timer;

/**
 * Displays the IME preferences inside the input method setting.
 */
public class ShareSetting extends Activity
{
    RadioButton rbtn_1 ;
    RadioButton rbtn_2 ;
    TextView tv_share_message ;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.share_setting);
        rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1) ;
        rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2) ;
        tv_share_message = (TextView)findViewById(R.id.tv_share_message);

        refresh() ;
    }

    private void refresh()
    {
        tv_share_message.setText(Const.getAttachMessage());
        if (Const.isAtachOk()) {
            rbtn_1.setChecked(true);
            rbtn_2.setChecked(false);
        }
        else
        {
            rbtn_1.setChecked(false);
            rbtn_2.setChecked(true);
        }
    }

    @Override
    protected void onResume() 
    {
        super.onResume();
    }

    public void rbtn_1_pressed(View v)
    {
         Const.saveAttachOk(true);
        refresh();
    }

    public void rbtn_2_pressed(View v)
    {
        Const.saveAttachOk(false);
        refresh();
    }

}

 