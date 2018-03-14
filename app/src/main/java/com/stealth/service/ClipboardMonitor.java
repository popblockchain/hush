package com.stealth.service;

import com.stealth.common.AppPrefs;
import com.stealth.common.LogTag;
import com.stealth.hushkbd.Const2;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;
import com.stealth.hushkbd.R;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

/**
 * Starts a background thread to monitor the states of clipboard and stores
 * any new clips into the SQLite database.
 * <p>
 * <i>Note:</i> the current android clipboard system service only supports
 * text clips, so in browser, we can just save images to external storage
 * (SD card). This service also monitors the downloads of browser, if any
 * image is detected, it will be stored into SQLite database, too.   
 */
public class ClipboardMonitor extends Service implements LogTag {

	int       myState = 0 ;
	final int JOIN_RESULT = 20 ;

	/** Image type to be monitored */
	private static final String[] IMAGE_SUFFIXS = new String[] {
			".jpg", ".jpeg", ".gif", ".png"
	};
	/** Path to browser downloads */
	private static final String BROWSER_DOWNLOAD_PATH = "/sdcard/download";

	private NotificationManager mNM;
	//private MonitorTask mTask = new MonitorTask();
	private SharedPreferences mPrefs;

	//int count = 0 ;

	public boolean bDspSuccess = true ;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		Const.MyLog("service onCreate Start");
		super.onCreate();

		Intent intent = new Intent(this, EnvSettings.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// Build notification

		String title ;
		String content ;

		title = getResources().getString(R.string.app_name);
		content = getResources().getString(R.string.app_noti_content_lite);

		Notification note = new NotificationCompat.Builder(this)
				.setContentTitle(title)
				.setSmallIcon(R.drawable.icon_small)
				.setContentText(content)
				.setContentIntent(pIntent).build();

		startForeground(1, note);


		Const.service1 = this ;
		Const.openDB(this);

		Const.userArrs = Const.dbDao.getUsers() ;

		if (Const.envSettings != null) {
			Const.envSettings.setTitleEnv();
			Const.envSettings.resumeJob();
		}

		if (Const2.appKind != Const.APP_LITE) {
			if (Const.isSecondUse())
				getGroupInfos(0);
		}

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Const.mCM = (android.content.ClipboardManager) Const.getService()
				.getSystemService(Const.getService().CLIPBOARD_SERVICE);
		mPrefs = getSharedPreferences(AppPrefs.NAME, MODE_PRIVATE);
		AppPrefs.operatingClipboardId = mPrefs.getInt(
				AppPrefs.KEY_OPERATING_CLIPBOARD,
				AppPrefs.DEF_OPERATING_CLIPBOARD);
		//AES256.setKey(Const.DefaultKey);
		// mTask.start();
        
        /*
        Const.DefaultKey = KeyStoreUtil.getKeyStrFromStore("Stealth");
        if (Const.DefaultKey.equals(""))
        {
            Const.DefaultKey = KeyStoreUtil.generateAESKeyStr() ;
            if (!Const.DefaultKey.equals(""))
            	KeyStoreUtil.saveKeyStrToStore("Stealth", Const.DefaultKey);
        }
        */

		Const.mCM.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener()
		{
			@Override
			public void onPrimaryClipChanged()
			{
				doTask();
			}
		});

		if (Const.envSettings != null)
			Const.envSettings.checkUser();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
//        Const.MyLog("MyService startCommand", "Received start id " + startId + ": " + intent);
		return START_STICKY; // run until explicitly stopped.
	}
	

    /*
	 public void doDayPassedTask() 
    {
    	new DayPassedTask().execute() ;
    }
    
	private class DayPassedTask extends AsyncTask<String, Integer, Integer> 
	{

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				int n = ReqParser.reqUserCheck() ;
					
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
			if (result == 0)
				getGroupInfos() ;
		}
	}
	*/


	/*
	public void gotoJoin()
	{
        Intent intent = new Intent(ClipboardMonitor.this, Join.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);        
        startActivity(intent);
 
	}
	*/

	public void gotoMain()
	{
		Intent intent = new Intent(ClipboardMonitor.this, EnvSettings.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	

    /*
	public void startTimer()
	{
		
		if (Const.timer != null)
			return ;
		
	    //Const.MyLog("Main", "startTimer");
		Const.timer = new Timer();

        Const.timer.scheduleAtFixedRate(new TimerTask()
        { 
    	   @Override
    	
    	   public void run() 
    	   {
    		        onTimerTick();
    	   }
       }, 0, 5000); // 5 seconds interval
	}
	*/
    
	/*
    private void onTimerTick() 
    {
    	try
    	{
    		//if (Const.timerCnt % 10 == 0)
    		{
    		    Const.checkDayPassed();
                System.out.println("Const.timerCnt="+Const.timerCnt);
    		}
    		
    		//ysk20160112
    		if (!Const.isMemberAgreeProcess)
    		{
            	if (Const.bNeedReqGroups)
            	{
            		long time = Util.getCurTime() ;
            		if (time-Const.lastGetGroupInfosTime > Const.TimeOutGetGroupInfos)
            		{
            			Const.lastGetGroupInfosTime = Util.getCurTime() ;
            		    Const.bNeedReqGroups = false ;
            		    //new ReqGroupListTask().execute();
                        Const.service.getGroupInfos() ;
            		}
            	}

            	else if (Const.groupArrs.isEmpty() &&
        				Const.service != null && 
        				Const.reqGroupListCnt == 0 &&
        				Const.getMySid() > 0 
        			    )
        		{
    				if (Const.timerCnt % 100 == 0) // 1 time per 500 sec
                        Const.service.getGroupInfos() ;
                }
    		}
            Const.timerCnt = Const.timerCnt + 1;
    	}
    	catch (Exception e)
    	{
    		
    	}
    }
    */

	@Override
	public void onDestroy() {
		Const.MyLog("ClipboardMonitor onDestory");
		mNM.cancel(R.string.clip_monitor_service);
		//mTask.cancel();
		Const.closeDB();
		Const.service1 = null ;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		Const.MyLog("ClipboardMonitor onStart");
	}

	/**
	 * Monitor task: monitor new text clips in global system clipboard and
	 * new image clips in browser download directory
	 */
	public void doTask()
	{
		// don't dsplay popup for just copied text from MainActivity
		Const.MyLog("doTask-------------------------------------------------------");
		Const.errCode = 0 ;

		long now = System.currentTimeMillis();
		if (now < Const.copyTime + 500)
			return ;
        
        /*
    	if (Const.getMySid() == 0)
    	{
    		gotoJoin() ;
    		return ;
    	}
    	*/

		Const.bFromKbd = false ;
		String newClip = Const.getClipText();
		if (!newClip.equals(""))
		{
			int level =-2;
			if ((level=Const.levelEncryptedText(newClip)) >= -1)
			{
				try
				{
					Const.curClip = newClip;
					Const.saveDspPopup(Const.DspDecryptedText) ;
					Const.startPopup(this) ;
				}
				catch (Exception e)
				{}
			}
			else // pure text
			{
				if (!Const.isEncryptWhenCopy())
					return ;

				Const.curClip = newClip ;

				if (!Const.curClip.equals("") && !Const.isEncryptWhenCopy())
				{
				}
				else
				{
						Const.saveDspPopup(Const.DspPureText);
						Const.startPopup(this);
				}
			}
		}
	}

	public String strR(int id)
	{
		return(getResources().getString(id));
	}


	public void reqRegMember(GroupInfo info, int stat)
	{
		Const.isReqProcess = true ;
		myState = stat ;
		new RegMemberTask().execute(info.GroupSid+"", Const.getMySid()+"", stat+"");
	}


	public void reqUserCheck(int plusHP)
	{
		if (Const.getMySid() > 0)
			new ReqUserCheckTask().execute(plusHP+"") ;
	}

	private class ReqUserCheckTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... strData) {
			int n = 0 ;
			try {
				n = ReqParser.reqUserCheck(Integer.parseInt(strData[0]));
				if (n != 0)
					return n;
			} catch (Exception e) {
				return 99;
			}
			return n;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 0)
			{
				// check if kbd mode
				checkYoutubUrl() ;
				if (Const.envSettings != null) {
					if (Const.isLock())
						Const.envSettings.checkLock();
					else
						Const.envSettings.checkKbdMode();
				}
				if (Const.puzzle != null)
					Const.puzzle.refresh() ;
			}
			if (result == 9) {
				if (Const.envSettings != null)
					Const.dspMustUpgrade(Const.envSettings);
				return;
			}

                /*
                else if (Const.getMySid() == 0)
                {
                    // need to regist user
                    reqRegUser() ;
                    //gotoJoin() ;
                }
                */

			if (Const.envSettings != null)
				Const.envSettings.refresh();
		}
	}

	private void checkYoutubUrl()
	{
		int level = Const.getMyOrgLevel() ;
		if (Const.getYoutubeUrl(level).equals(""))
		{
			new ReqYoutubeTask().execute(level+"") ;
		}
	}

	private class ReqYoutubeTask extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... strData)
		{
			try
			{
                int level = Integer.parseInt(strData[0]);
				int n = ReqParser.reqYoutubeUrl(level) ;
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
			if (result == 0)
			{
			}
		}
	}

	private class RegMemberTask extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... strData)
		{
			try
			{
				int n = ReqParser.reqRegMember(strData[0], strData[1], strData[2]) ;
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
			if (result == 0)
			{
				if (Const.envSettings != null)
					Const.envSettings.finish() ;
			}
			Const.isReqProcess = false ;
		}
	}







	public void getGroupInfos(int groupSid)
	{
		//if //(!Const.isMemberAgreeProcess &&
		if (!Const.isReqProcess &&
				Const.getService() != null
				)
		{
			Const.isReqProcess = true ;
			new GetGroupInfosTask().execute(groupSid+"");
		}
	}

	private class GetGroupInfosTask extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... strData)
		{
			try
			{
				int n = 0 ;

				if (!Const.isUserChecked) {
					Const.isUserChecked = true ;
					n = ReqParser.reqUserCheck(0);
					if (n != 0)
						return n;
				}

				if (Const.getMySid() == 0)
					return 99 ;

				int groupSid = Integer.parseInt(strData[0]) ;
				if ((groupSid == 0) || // all group list
						Const.groupArrs.isEmpty())
					Const.groupArrs = Const.dbDao.getGroups() ;
				n = ReqParser.reqGroupVersList1(groupSid) ;
				if (n==0)
				{
					if (groupSid == 0) // all group list
						Const.arrangeGroupList();
				}
				Const.isReqProcess = false ;
				return n ;
			}
			catch(Exception e)
			{
				Const.isReqProcess = false ;
				return 99;
			}
		}


		@Override
		protected void onPostExecute(Integer result)
		{
			//if (result == 0) // reqSendInfo success
			{
				Const.isReqProcess = false ;

				//ysk20161103
				/*
				if (result == 9)
				{
					if (Const.envSettings == null) {
						Const.mustUpgrade = true;
						gotoMain();
					}
					else
					    Const.dspMustUpgrade(Const.envSettings) ;
					return ;
				}
				*/

				if (Const.groupList != null)
					Const.groupList.refreshList() ;
				else if (Const.groupSelect != null)
					Const.groupSelect.refreshList();
			}
		}
	}



	/*
	public void reqGroupKey(int groupSid, int level, int seq)
	{
		new ReqGroupKeyTask().execute(groupSid+"", level+"", seq+"");
	}
    */
	/*
	public class ReqGroupKeyTask extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... strData)
		{
			try
			{
				int id = Integer.parseInt(strData[0]);
				int level = Integer.parseInt(strData[1]);
				int seq = Integer.parseInt(strData[2]);
				int n = ReqParser.reqGroupKey1(id, level, seq) ;
				if (n==0)
					Const.MyLog("ReqGroupKeyTask key=", Const.getGKey(id)+"");
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
		}
	}
	*/

	/*
	public void regGroupInfo(int groupSid)
	{
		new ReqGroupInfoTask().execute(groupSid+"");
	}

	public class ReqGroupInfoTask extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... strData)
		{
			try
			{
				int id = Integer.parseInt(strData[0]);
				int n = ReqParser.reqGroupInfo1(id) ;
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
		}
	}
	*/



 	/*
 	private class MonitorTask extends Thread {

        private volatile boolean mKeepRunning = false;
        
        public MonitorTask() {
            super("ClipboardMonitor");
        }

        public void cancel() {
            mKeepRunning = false;
            interrupt();
        }
        
        @Override
        public void run() {
            mKeepRunning = true;
            while (true) {
//                doTask(true);
                try {
                    Thread.sleep(mPrefs.getInt(AppPrefs.KEY_MONITOR_INTERVAL,
                            AppPrefs.DEF_MONITOR_INTERVAL));
                        
                } catch (InterruptedException ignored) {
                }
                if (!mKeepRunning) {
                    break;
                }
            }
        }
        
    }
    */
}
