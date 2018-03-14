/*
 * Copyright (C) 2008-2009 The Android Open Source Project
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

package com.stealth.hushkbd;

import com.stealth.service.Const;
import com.stealth.hushkbd.*;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodSubtype;

public class LatinKeyboardView extends KeyboardView {

    static final int KEYCODE_OPTIONS = -100;
    // TODO: Move this into android.inputmethodservice.Keyboard
    static final int KEYCODE_LANGUAGE_SWITCH = -101;
    
    static final int KEYCODE_LANGUAGE_ENCRYPT = -9 ; //ysk
    static final int KEYCODE_GROUP_ID = -10 ; //ysk
    static final int KEYCODE_SETTINGS = -13 ; //ysk
    static final int KEYCODE_NUMERIC_SYMBOL = -7 ; //123/!@# key 
    static final int KEYCODE_HAN_HAN = -8 ; //ysk
    //static final int KEYCODE_DOT_DOT_DOT = -10 ; //ysk

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final com.stealth.hushkbd.LatinKeyboard keyboard = (com.stealth.hushkbd.LatinKeyboard)getKeyboard();
        keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }

    public void changeEncryptKey(com.stealth.hushkbd.LatinKeyboard kbd, boolean bCancelKey)
    {
    	// if hangul is composing enter key = dummy key
    	/*
    	if (bCancelKey)
    		kbd.mEncryptKey.icon = getResources().getDrawable(R.drawable.sym_keyboard_cancel);
    	else
    		kbd.mEncryptKey.icon = getResources().getDrawable(R.drawable.sym_keyboard_encrypt);
    	invalidate();
    	*/
    }

    public void dspGroupId(LatinKeyboard kbd)
    {
    	//if (Const.curGroupInfo != Const.prevGroupInfo)
    	{
    		if (kbd == null)
    			return ;
    		
    		if (Const.curGroupInfo == null)
    		{
    	       kbd.mGroupIdKey.label = getResources().getString(R.string.common_key) + " ▼" ;
    		}
    		else
    		{
        		String str = Const.curGroupInfo.GroupId ;
        		int len = str.length() ;
        		if (len > 5)
        			str = str.substring(0, 5) ;
     	       
        		kbd.mGroupIdKey.label = str + " ▼";
    		}
    		invalidateAllKeys();
    		//this.invalidateKey(KEYCODE_GROUP_ID);
    	}
    }
    

}
