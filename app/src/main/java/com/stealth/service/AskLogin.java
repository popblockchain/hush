/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.stealth.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stealth.hushkbd.R;
import com.stealth.util.Util;

/**
 * A simple launcher activity containing a summary stealth description, stealth log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the stealth log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class AskLogin extends Activity {

    public static final String TAG = "Help";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_login);
    }

    public void regist_pressed(View v)
    {
        // Do something in response to the click
        Const.needGotoRegistPhone = true ;
        finish();
    }

    public void login_pressed(View v)
    {
        // Do something in response to the click
        Const.needGotoLogin = true ;
        finish() ;
    }

    public void onBackPressed()
    {
        Const.needExit = true ;
        super.onBackPressed();
    }

    /** Create a chain of targets that will receive log data */
}
