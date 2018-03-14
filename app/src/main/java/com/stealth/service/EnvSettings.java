package com.stealth.service;

import android.content.ComponentName;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stealth.util.ReqParser;
import com.stealth.util.Util;
import com.stealth.hushkbd.Const1;
import com.stealth.hushkbd.R;

import java.util.Timer;
import java.util.TimerTask;

public class EnvSettings extends Activity
{
//	Button btn_recent ;
//	Button btn_saved ;
    //ysk20161103
    String sNotice = "" ;
    boolean bMenuOn = false ;
    boolean bNoticed = false ;

    boolean curKbdEncrypt ;

    boolean UserChecked = false ;
    ImageButton btn_lock ;

    ImageButton btn_copy_encrypt ;
    ImageButton btn_kbd_encrypt ;
    ImageButton btn_timer ;
    ImageButton btn_share ;
    Button btn_puzzle ;
    Button btn_help ;
    Button btn_password ;
    //ImageButton btn_information ;
    ImageButton btn_encrypt1 ;
    ImageButton btn_encrypt2 ;
    ImageView img_lock ;
    TextView tv_title ;
    TextView tv_timer ;
    Animation aniFadeIn ;
    Animation aniFadeOut ;
    TextView tv_lock_info ;
    LinearLayout ll_menu ;

	int target_ix ;

    boolean bInitialized = false ;
    int permissionNo = 0 ;
    boolean isPermissionRequesting = false ;
    boolean isPermissionOk = false ;

    Timer timer = null ;
    Timer timer_lock = null ;
    Timer timer_kbd = null ;
    int timer_cnt_lock = 0 ;
    int timer_cnt_kbd = 0 ;

    final int PASSWORD_RESULT = 50 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Const.envSettings = this ;

        Const.initPuzzles();

        if (Util.isKoreanLanguage())
           setContentView(R.layout.env_settings);
        else
           setContentView(R.layout.env_settings_no_kbd);

        btn_lock = (ImageButton) findViewById(R.id.btn_lock) ;
        btn_help = (Button) findViewById(R.id.btn_help) ;
        btn_puzzle = (Button) findViewById(R.id.btn_puzzle) ;
        btn_password = (Button) findViewById(R.id.btn_password) ;

        btn_copy_encrypt = (ImageButton) findViewById(R.id.btn_copy_encrypt) ;
        btn_encrypt1 = (ImageButton) findViewById(R.id.btn_encrypt1) ;

        img_lock = (ImageView) findViewById(R.id.img_lock) ;

        if (Util.isKoreanLanguage()) {
            btn_kbd_encrypt = (ImageButton) findViewById(R.id.btn_kbd_encrypt);
            btn_encrypt2 = (ImageButton) findViewById(R.id.btn_encrypt2) ;
        }
        btn_timer = (ImageButton) findViewById(R.id.btn_timer) ;
        btn_share = (ImageButton) findViewById(R.id.btn_share) ;
        tv_title = (TextView) findViewById(R.id.tv_title) ;
        tv_timer = (TextView) findViewById(R.id.tv_timer) ;
        tv_lock_info = (TextView) findViewById(R.id.tv_lock_info) ;

        aniFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        aniFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        ll_menu = (LinearLayout)findViewById(R.id.ll_menu) ;
        ll_menu.setVisibility(View.INVISIBLE);

        Const.isUserChecked = false ;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Const.getService() != null) {
            refresh(); //setTitleEnv();

            if (!Const.curClip.equals("") && !Const.isEncryptWhenCopy()) {
                if ((Const.levelEncryptedText(Const.curClip)) < -1)
                    gotoPopupEncrypt();
            }
        }
    }

    public void encrypt_pressed(View v)
    {
        gotoPopupEncrypt();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (!Const.isLock())
            Const.saveLastUsedTime();
    }

    @Override
    protected void onResume( )
    {
        super.onResume();
        if (Const.service1 == null)
            startClipboardMonitor();
        else {
            resumeJob();
            refresh() ;
        }
    }

    public void resumeJob()
    {
        sNotice = Const.getNotice(); //ysk20161103
        String sViewedNotice = Const.getViewedNotice() ;

        if (!Const.isSecondUse())
        {
            Const.saveSecondUse();
            gotoHelp();
            return ;
        }
        else if (timer != null)
            return ; // wait until permission finished
        else if (!checkDangerousPermission())
        {
            startTimer();
            return ;
        }
        else if (Const.needAskLogin) // login or regist
        {
            Const.needAskLogin = false ;
            gotoAskLogin() ;
            return ;
        }
        else if (Const.needGotoLogin)
        {
            Const.needGotoLogin = false ;
            gotoLogin() ;
            return ;
        }
        else if (Const.needGotoRegistPhone)
        {
            Const.needGotoRegistPhone = false ;
            gotoRegistPhone();
            return ;
        }
        else if (Const.needGotoRegistPass)
        {
            Const.needGotoRegistPass = false ;
            gotoRegistPass();
            return ;
        }
        else if (Const.needDspLoginHelp)
        {
            Const.needDspLoginHelp = false ;
            dspLoginHelp() ;
            return ;
        }
        else if (Const.needExit) //ysk20161103 order change
        {
            Const.needExit = false ;
            finish() ;
        }
        else if (Const.mustUpgrade()) //ysk20161103 mustUpgrade)
        {
            Const.needExit = true ;
            Const.dspMustUpgrade(this);
            return ;
        }
        //ysk20161103
        else if (!bNoticed && !sNotice.equals("") && !sViewedNotice.equals(sNotice))
        {
            bNoticed = true ;
            dspNotice() ;
        }
        else if (Const.getMySid() == 0)
        {
            gotoAskLogin() ;
        }
        else if (Const.needGotoPuzzle)
        {
            Const.needGotoPuzzle = false ;
            gotoPuzzle();
        }
        else {
            checkUser();

            refresh();
        }
        //Const.checkGroupInvitation(this);
    }

    @Override
    public void onDestroy()
    {
        if (!Const.isLock())
            Const.saveLastUsedTime();

        Const.envSettings = null ;

        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (Const.getService() != null)
            refresh() ;
        if (curKbdEncrypt != Const.isEncryptByKbd())
        {
            stopTimerKbd();
        }
    }

    private void startTimer()
    {
        if (timer != null)
            return ;

        timer = new Timer();

        timer.scheduleAtFixedRate(
                new TimerTask()
                {
                    @Override

                    public void run()
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                onTimerTick();
                            }
                        });

                        if (timer == null)
                        {
                            return;
                        }

                    }
                }, 0, 500); // 0.5 seconds interval
    }

    private void stopTimer()
    {
        if (timer != null)
            timer.cancel();
        timer = null ;
    }

    private void onTimerTick() {
        // rotate Settings wheel
        boolean finishTimer = false ;
        if (isPermissionOk) {
            finishTimer = true;
        }
        else if (setDangerousPermission()) {
                    finishTimer = true ;
                }


        if (finishTimer)
        {
            stopTimer();
            if (Const.getMySid() == 0)
            {
                gotoAskLogin();
            }
        }
    }

    private void startTimerKbd()
    {
        if (timer_kbd != null)
            return ;

        timer_cnt_kbd = 0 ;

        timer_kbd = new Timer();

        timer_kbd.scheduleAtFixedRate(
                new TimerTask()
                {
                    @Override

                    public void run()
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                onTimerTickKbd();
                            }
                        });

                        if (timer_kbd == null)
                        {
                            return;
                        }

                    }
                }, 0, 200); // 0.2 seconds interval
    }

    private void onTimerTickKbd() {
        timer_cnt_kbd++ ;
        if ((timer_cnt_kbd % 4) <= 1)
            btn_kbd_encrypt.setAlpha((float)0.3);
        else
            btn_kbd_encrypt.setAlpha((float)1.0);
    }

    private void stopTimerKbd()
    {
        if (timer_kbd != null)
        {
            timer_kbd.cancel();
            timer_kbd = null ;
            btn_kbd_encrypt.setAlpha((float)1.0);
        }
    }

    private void startTimerLock()
    {
        if (timer_lock != null)
            return ;

        timer_cnt_lock = 0 ;

        timer_lock = new Timer();

        timer_lock.scheduleAtFixedRate(
                new TimerTask()
                {
                    @Override

                    public void run()
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                onTimerTickLock();
                            }
                        });

                        if (timer_lock == null)
                        {
                            return;
                        }

                    }
                }, 0, 200); // 0.1 seconds interval
    }

    private void onTimerTickLock() {
        timer_cnt_lock++ ;
        if ((timer_cnt_lock % 4) <= 1)
            btn_lock.setAlpha((float)0.3);
        else
            btn_lock.setAlpha((float)1.0);
    }

    private void stopTimerLock()
    {
        if (timer_lock != null)
        {
            timer_lock.cancel();
            timer_lock = null ;
            btn_lock.setAlpha((float)1.0);
        }
    }

    //-------------------------------------------
    private boolean checkDangerousPermission()
    {
        // check only
        if (isPermissionOk)
            return true ;

        if (Build.VERSION.SDK_INT > 9 && Build.VERSION.SDK_INT < 23 )
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
            isPermissionOk = true ;
            return true ;
        }

        if (Build.VERSION.SDK_INT >= 23)
        {
                    if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0)
                    {
                        return false ;
                    }
                    if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0)
                    {
                        return false ;
                    }
        }
        return true ;
    }

    private boolean setDangerousPermission()
    {
                switch (this.permissionNo)
                {
                    case 0 :
                        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0)
                        {
                            // not apporved
                            if (!isPermissionRequesting)
                            {
                                isPermissionRequesting = true ;
                                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_PHONE_STATE"}, 0);
                            }
                        }
                        else
                        {
                            // approved
                            isPermissionRequesting = false ;
                            permissionNo ++ ;
                        }
                        break ;

                    case 1 :
                        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0)
                        {
                            // not apporved
                            if (!isPermissionRequesting)
                            {
                                isPermissionRequesting = true ;
                                ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
                            }
                        }
                        else
                        {
                            // approved
                            isPermissionRequesting = false ;
                            permissionNo ++ ;
                            isPermissionOk = true ;
                            return true ;
                        }
                        break ;
                }

        return false  ;
    }

    private void askPrevDsp()
    {
        String str = getResources().getString(R.string.puzzle_view_prev) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Const.curLevel = Const.getMyOrgLevel()-1 ;
                        Const.prevPuzzleState = true ;
                        gotoPuzzle();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Const.curLevel = Const.getMyOrgLevel() ;
                        Const.prevPuzzleState = false ;
                        gotoPuzzle();
                    }
                })
                .show();
    }

    public void level_pressed(View v) {
        offMenu();
        if (Const.getMyName().equals(""))
            askPuzzle();
        else
        {
            Const.savePuzzleNum(-1);
            int level = Const.getMyOrgLevel() ;
            if ((level > 0) && (Const.getMyPartsCnt(level) == 0)) { //ysk20161127
                // display prev video
                //ysk20161028
                askPrevDsp() ;
            }
            else
            {
                Const.curLevel = level ;
                gotoPuzzle() ;
            }
        }
    }


    private void askPuzzle()
    {
        String str = getResources().getString(R.string.regist_name_ask);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage(str);
        alert.setCancelable(false);

        DialogInterface.OnClickListener OKListener = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                gotoRegistName();
            }
        };

        alert.setPositiveButton(getResources().getString(R.string.ok), OKListener);
        alert.show();

    }

    public void timer_pressed(View v)
    {
        offMenu();
        goto_time_out() ;
    }

    public void copy_encrypt_pressed(View v)
    {
        offMenu();
        String str ;

        Const.saveEncryptWhenCopy(!Const.isEncryptWhenCopy());
//        btn_copy_encrypt.startAnimation(aniFadeOut);

        refresh() ;
        btn_copy_encrypt.startAnimation(aniFadeIn);


        if (!Const.isEncryptWhenCopy())
        {
            str = getResources().getText(R.string.encrypt_when_guide_off).toString();
        }
        else
        {
            str = getResources().getText(R.string.encrypt_when_guide_on).toString();
        }
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    private void dspNeedResetDefault()
    {
        String str = getResources().getString(R.string.need_reset_default);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage(str);
        alert.setCancelable(false);

        DialogInterface.OnClickListener OKListener = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                Const1.selectDefaultKeyboard(true);
            }
        };

        alert.setPositiveButton(getResources().getString(R.string.ok), OKListener);
        alert.show();

    }

    private void dspNeedKbdSetting()
    {
        String str = getResources().getString(R.string.need_kbd_setting);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage(str);
        alert.setCancelable(false);

        DialogInterface.OnClickListener OKListener = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                gotoKbdSetting();
            }
        };

        alert.setPositiveButton(getResources().getString(R.string.ok), OKListener);
        alert.show();

    }

    private void gotoRegistName()
    {
        Intent intent = new Intent(this, RegistName.class);
        startActivity(intent);
    }

    private void gotoKbdSetting()
    {
        Intent intent = new Intent(this, KbdSetting.class);
        startActivity(intent);
    }

    public void kbd_encrypt_pressed(View v)
    {
        offMenu();
        stopTimerKbd();
        startTimerKbd();

        curKbdEncrypt = Const.isEncryptByKbd() ;

        if (curKbdEncrypt)
        {
            offEncryptByKbd() ;
        }
        else
        {
            onEncryptByKbd() ;
        }
    }

    public void offEncryptByKbd()
    {
            dspNeedResetDefault() ;
    }

    public void onEncryptByKbd()
    {
            dspNeedKbdSetting();
    }

    public void checkUser() {
        if (Const.service1 != null) {
            if (UserChecked)
                return;
                UserChecked = true ;
                Const.service1.reqUserCheck(0);
        }
    }

    private void startClipboardMonitor()
    {
        if (Const.service1 == null)
        {
            ComponentName service = startService(
                    new Intent(this, ClipboardMonitor.class));
            if (service == null)
            {
                Const.MyLog("Can't start service "
                        + ClipboardMonitor.class.getName());
            }
            //startService(new Intent(this,
            //     ClipboardMonitor.class));
        }
    }

    public void onBackPressed()
    {
        if (bMenuOn)
            offMenu();
        else
    	    super.onBackPressed();
    	/*
    	 FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) ll_main.getLayoutParams();
         lp2.width = 300;
         lp2.height =400;
         ll_main.setLayoutParams(lp2);
         */


    	//ll_main.setVisibility(View.INVISIBLE);
    }


    public String strR(int id)
    {
    	return(getResources().getString(id));
    }

    private void dspLoginHelp()
    {

        String str = getResources().getString(R.string.lock_help) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Const.needKbdOn = true ;
                    }
                })
                .show();
    }

    public void gotoHelp()
    {
        Intent intent = new Intent(this, Help.class);

        startActivity(intent);
    }

    public void gotoMyInfo()
    {
        Intent intent = new Intent(this, MyInfo.class);

        startActivity(intent);
    }

    public void gotoPuzzle()
    {
        Intent intent = new Intent(this, Puzzle.class);

        startActivity(intent);
    }

    public void goto_time_out()
    {
        Intent intent = new Intent(this, TimeOut.class);

        startActivity(intent);
    }

    public void gotoAskLogin()
    {
        Intent intent = new Intent(this, AskLogin.class);

        startActivity(intent);
    }

    public void gotoRegistPhone()
    {
        Intent intent = new Intent(this, RegistPhone.class);

        startActivity(intent);
    }

    public void gotoRegistPass()
    {
        Intent intent = new Intent(this, RegistPass.class);

        startActivity(intent);
    }

    public void gotoLogin()
    {
        Intent intent = new Intent(this, Login.class);

        startActivity(intent);
    }

    @Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) 
	{
        switch (reqCode)
        {
            case PASSWORD_RESULT :
                if (resultCode == RESULT_OK)
                {
                    Const.saveLock(false);
                    refresh() ;
                }
                break ;

            case 100 :
            if (resultCode == RESULT_OK)
            {
                UserChecked = false ;
                // check again
                checkUser();
            }
        }
	}


    private void gotoPopupEncrypt() {
        Intent intent1 = new Intent(this, PopupEncrypt.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Const.getService().startActivity(intent1);
    }

    private void gotoChangePassword() {
        Intent intent1 = new Intent(this, ChangePass.class);
        startActivity(intent1);
    }


    public void checkKbdMode()
    {
        if (Util.isKoreanLanguage() && !Const.isEncryptByKbd())
        {
            if (!Const.isKbdModeAsked())
            {
                Const.saveKbdModeAsked(true);
                askKbdMode() ;
            }
        }
    }

    public void checkLock()
    {
        if (Const.getMySid() <= 0)
            return ;

        if (Const.isLock())
        {
            if (!Const.isLockAsked())
            {
                Const.saveLockAsked(true);
                askUseLock() ;
            }
        }
    }

    private void askKbdMode()
    {
        //
        String str = getResources().getString(R.string.encrypt_method_kbd_ask) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        startTimerKbd() ;
                    }
                })
                .show();
    }

    private void askUseLock()
    {
        //
        String str = getResources().getString(R.string.lock_help) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        startTimerLock() ;
                    }
                })
                .show();
    }
       /*
        private void reqRegUser()
        {
            new ReqRegUserTask().execute(Util.getCountryZipCode());
        }

        private class ReqRegUserTask extends AsyncTask<String, Integer, Integer>
        {

            @Override
            protected Integer doInBackground(String... strData)
            {
                try
                {
                    int n = ReqParser.reqRegUser(strData[0]) ;
                    return n ;
                }
                catch(Exception e)
                {
                    return 99;
                }
            }

            @Override
            protected void onPostExecute(Integer result)
            {
                if (result == 0) // success
                {
                }
                //startTimer();
            }
        }
    */

    private void gotoPhoneNumber()
    {
        Intent intent = new Intent(EnvSettings.this, PhoneNumber.class);

        startActivityForResult(intent, 100);

    }

    public void refresh()
    {
        setTitleEnv();

        //ysk20161103
        btn_help.setText(R.string.app_help);
        btn_password.setText(R.string.change_pass);

        if  (!Const.isLock())
            {
                tv_lock_info.setVisibility(View.INVISIBLE);
                img_lock.setVisibility(View.INVISIBLE);
                if (Const.isEncryptWhenCopy())
                    btn_copy_encrypt.setImageResource(R.drawable.on);
                else
                    btn_copy_encrypt.setImageResource(R.drawable.off);

            btn_lock.setImageResource(R.drawable.unlock);
            //btn_information.setVisibility(View.VISIBLE);
            btn_puzzle.setVisibility(View.VISIBLE);
            btn_puzzle.setText(Const.getCryptoSymbolFromLevel(Const.getMyOrgLevel()));
            btn_copy_encrypt.setVisibility(View.VISIBLE);
            btn_encrypt1.setVisibility(View.VISIBLE);

            if (Util.isKoreanLanguage()) {
                if (Const.isEncryptByKbd())
                    btn_kbd_encrypt.setImageResource(R.drawable.on);
                else
                    btn_kbd_encrypt.setImageResource(R.drawable.off);

                btn_kbd_encrypt.setVisibility(View.VISIBLE);
                btn_encrypt2.setVisibility(View.VISIBLE);
            }
            btn_timer.setVisibility(View.VISIBLE);
                tv_timer.setVisibility(View.VISIBLE);
                tv_timer.setText(Const.getTimeOutText());
            btn_share.setVisibility(View.VISIBLE);
        }
        else {
            tv_lock_info.setVisibility(View.VISIBLE);
            if (Const.getLock() == true)
                tv_lock_info.setText(strR(R.string.locked_user));
            else
                tv_lock_info.setText(strR(R.string.locked_auto));

            img_lock.setVisibility(View.VISIBLE);
            btn_lock.setImageResource(R.drawable.lock);
            btn_puzzle.setVisibility(View.INVISIBLE);
            btn_copy_encrypt.setVisibility(View.INVISIBLE);
            btn_encrypt1.setVisibility(View.INVISIBLE);
            if (Util.isKoreanLanguage()) {
                btn_kbd_encrypt.setVisibility(View.INVISIBLE);
                btn_encrypt2.setVisibility(View.INVISIBLE);
            }
            btn_timer.setVisibility(View.INVISIBLE);
            btn_share.setVisibility(View.INVISIBLE);
            tv_timer.setVisibility(View.INVISIBLE);
        }
        checkLock() ;
    }

    private void startAnimation() {
        Animation rotate_size_ani =
                AnimationUtils.loadAnimation(this, R.anim.rotate_size_anim);
        //iv_level.setAnimation(rotate_size_ani);
    }

    public void setTitleEnv() {
        String str = getResources().getString(R.string.app_name) + " " + Util.getAppVersion() ;
        setTitle(str);
        tv_title.setText(str);
    }

    public void share_pressed(View v)
    {
        offMenu();
        Intent intent = new Intent(EnvSettings.this, ShareSetting.class);

        startActivity(intent);
    }

    private void dspLockState()
    {
        String str = getResources().getText(R.string.lock_state_locked).toString();
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    public void lock_pressed(View v)
    {
        offMenu();
        stopTimerLock();

        if (Const.getMySid() > 0)
        {
            if (Const.isLock())
            {
                askUnlock() ;
            }
            else {
                Const.saveLock(true);
                dspLockState() ;
            }
            refresh();
        }
    }


    private void askUnlock()
    {
        // input pass word and unlock
        Intent intent = new Intent(EnvSettings.this, PasswordAsk.class);

        startActivityForResult(intent, PASSWORD_RESULT);
    }

    Intent intentPasswordAsk ;

    private void gotoPasswordAsk() {
        offMenu();
        intentPasswordAsk = new Intent(EnvSettings.this, PasswordAsk.class);
        intentPasswordAsk.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intentPasswordAsk, PASSWORD_RESULT);
    }


    //ysk20161103
    public void password_pressed(View view) {
        ll_menu.setVisibility(View.INVISIBLE);
        gotoChangePassword();
    }

    public void offMenu()
    {
        if (bMenuOn)
        {
            bMenuOn = false ;
            ll_menu.setVisibility(View.INVISIBLE);
        }
    }

    public void menu_pressed(View view) {
        if (!bMenuOn) {
            if (!Const.isLock()) {
                bMenuOn = true ;
                ll_menu.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            bMenuOn = false ;
            ll_menu.setVisibility(View.INVISIBLE);
        }
    }

    public void help_pressed(View view) {
        ll_menu.setVisibility(View.INVISIBLE);
        gotoHelp();
    }

    public void my_info_pressed(View view) {
        ll_menu.setVisibility(View.INVISIBLE);
        gotoMyInfo();
    }


    //ysk20161103
    private void dspNotice()
    {
        String str = getResources().getString(R.string.notice_title) ;
        new AlertDialog.Builder(this).setMessage(sNotice)
                .setTitle(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .setNegativeButton(R.string.dsp_no_more, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Const.saveViewedNotice(sNotice);
                    }
                })
                .show();
    }
}