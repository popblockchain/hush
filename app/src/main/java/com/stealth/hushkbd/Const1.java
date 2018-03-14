package com.stealth.hushkbd;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.stealth.service.Const;
import com.stealth.hushkbd.R;

import java.util.List;


/**
 * Created by kim on 3/5/2016.
 */
public class Const1
{
    //public static final String StealthIme = "com.stealth.hush.lite/com.stealth.hushkbd.hush_kbd";

    public static boolean EnglishMode = false ;
    // Special -> Han/Eng ��
    // EnglishMode     true     qwerty.xml
    //                 false    by HangulKbdType qwerty_korean, google, chunjiin

    public static final int HAN_QWERTY = 1 ;
    public static final int HAN_CHUNJIIN = 2 ;
    public static final int HAN_GOOGLE = 3 ;

    public static int prevCode = 0 ; // for autoshift

    public static InputConnection curInputConnection = null ;

    public static int getHangulKbdType()
    {
        // 1==qwerty, 2=chunjiin, 3=google
        if (Const.getService() == null)
            return HAN_GOOGLE ;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
        int n = prefs.getInt("KoreanKeyboardType", HAN_GOOGLE);
        return (n) ;
    }

    public static void setHangulKbdType(int n)
    {
        if (Const.getService() == null)
            return  ;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("KoreanKeyboardType", n);
        editor.commit();
    }
    public static boolean isKbdEncryptMethod()
    {
        return (isStealthSelected() && Const.isStealthDefault()) ;
    }

    public static boolean isStealthSelected()
    {
        InputMethodManager imm = (InputMethodManager)Const.getService().getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();

        final int N = mInputMethodProperties.size();

        for (int i = 0; i < N; i++)
        {

            InputMethodInfo imi = mInputMethodProperties.get(i);
            String str = imi.getId() ;

            Const.MyLog("isStealthSelected", "imi="+str);

            if (str.equals(Const.getStealthIme()))
            {
                //imi contains the information about the keyboard you are using
                return true;
            }
        }
        return false ;
    }


    public static boolean bDspSuccess = true ;


    public static void selectDefaultKeyboard(boolean bForReset)
    {
        if (!isStealthSelected() && (!bForReset))
        {
            Toast.makeText(Const.getService(), Const.getService().getResources().getString(R.string.select_first), Toast.LENGTH_LONG).show();
            return ;
        }
        bDspSuccess = false ;
        InputMethodManager imeManager = (InputMethodManager) Const.getService().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        } else {
            Toast.makeText(Const.getService() ,"Error", Toast.LENGTH_LONG).show();
        }
    }


}
