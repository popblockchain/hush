package com.stealth.service;

import java.util.ArrayList;

import com.stealth.service.Const.ContactUser;
import com.stealth.hushkbd.R;
import com.stealth.util.FileFunc;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MemberList extends ListActivity 
{
	public static final int  PICK_CONTACT = 1 ;

	static final int ACTIVITY_CHOOSE_FILE = 10 ;
	String filePath ;
    FileFunc func ;

    //	Button btn_recent ;
//	Button btn_saved ;
	EditText et_search ;
	MemberInfo askInfo ;
	static MemberMakeDialog mMemberMakeDialog ;
	static EditText et_member_no ;
	static EditText et_member_nm ;
	String member_no ;
	String member_nm ;
	Button btn_delete_group ;
	Button btn_member_regist ;
	
	public static final int  GROUP_REG_RESULT = 20 ;
	ListView lv ;
	
	int target_ix ;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list);
        
		Const.selectedContacts = new ArrayList<ContactUser>();
		Const.clearSelected(); 

		MemberListAdapter cAdapter = new MemberListAdapter(this); 
        setListAdapter(cAdapter);
        
        lv = (ListView)findViewById(android.R.id.list);
        et_search = (EditText)findViewById(R.id.et_search);
        
        btn_delete_group = (Button)findViewById(R.id.btn_delete_group);
        btn_member_regist = (Button)findViewById(R.id.btn_member_regist);
        
        if (!Const.curGroupInfo.isGroupMgr())
        {
        	btn_delete_group.setVisibility(View.INVISIBLE);
        	btn_member_regist.setVisibility(View.INVISIBLE);
        }

        lv.setOnItemClickListener(null);
        
        Const.curGroupInfo.memberList = null ;
        reqMemberList("") ;

		setTitle(getResources().getString(R.string.member_list)+"("+Const.curGroupInfo.GroupId+")");
    }
    
    
    public String strR(int id)
    {
    	return(getResources().getString(id));
    }
    
	
    public void onBackPressed()
    {
	    if (Const.isReqProcess)
		    return ;
    	super.onBackPressed(); 
    }

	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) 
	{
		  switch (reqCode) 
		  {
	      case ACTIVITY_CHOOSE_FILE: 
	        if (resultCode == RESULT_OK)
	        {
	          Uri uri = data.getData();
	          filePath = uri.getPath();
	          readFphoneTextFile();
	        }
	        break ;
	        
		    case PICK_CONTACT :
		    	askRegistSelected() ;
		        break ;
	        	 
		  }
	}
	
	private void askRegistSelected() 
	{
		if (Const.selectedContacts.size() == 0)
			return ;
    
		String str = String.format(strR(R.string.member_selected_ask), Const.selectedContacts.size());
		new AlertDialog.Builder(this).setMessage(str) 
		.setCancelable(true)
		.setPositiveButton(strR(R.string.yes), new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// yes
			    registFromSelectedContacts();
			}
		})
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// no
			}
		})
		.show();
	}
    
    private void readFphoneTextFile()
    {
    	// file format = comma separated name,phone 
        func = new FileFunc() ;
        if (!func.openRead(filePath))
        	return ;
        
        new FileReadTask().execute() ;
        
    }
    
	private class FileReadTask extends AsyncTask<String, Integer, Integer> 
	{

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				boolean eof = false ;
				String userNames = "" ;
				String userPhones = "" ;
				
				while (!eof)
				{
					String str = func.readLine();
					if (str == null)
						eof = true ; // finish
					else
					{
				    	String sUserName ;
				    	String sUserPhone ;
				    	
				        String strs[] = str.split(",") ;

				        if (strs.length < 2)
				        {
				        	eof = true ;
				        }
				        else
				        {
					        sUserName = strs[0].trim() ;
					        sUserPhone = strs[1].trim() ;
					        
							String sPhone = Util.convGlobalPhoneNumber(sUserPhone);

							// 1. validate check of phone (starting with "+821", "01" == OK)
							if (sPhone.length() > 8  &&
								sUserName.length() >= 2)
							{
								if (userNames.equals(""))
									userNames = sUserName ;
								else
									userNames += ","+sUserName ;
								
								if (userPhones.equals(""))
									userPhones = sPhone ;
								else
									userPhones += ","+sPhone ;
							}
					    }
					}				
				}
				
				return (ReqParser.reqSubMember(Const.curGroupInfo.GroupSid, userNames, userPhones));
			} 
			catch(Exception e) 
			{
				return 99;
			}
		}
		
		
		@Override
		protected void onPostExecute(Integer result) 
		{
			if (result == 1) // error
			{
				// failed
			}
			else 
			{
		        reqMemberList("");
			}
		}
	}
	
    private void registFromSelectedContacts()
    {
    	// file format = comma separated name,phone 
        
        new RegistSelectedContactsTask().execute() ;
        
    }
    
	private class RegistSelectedContactsTask extends AsyncTask<String, Integer, Integer> 
	{
		ProgressDialog progress;

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				boolean eof = false ;
				String userNames = "" ;
				String userPhones = "" ;
				int MAX = 10 ;
				int nMember = 0 ;
				int ret = 0 ;

				for (ContactUser usr : Const.selectedContacts)
				{
			    	String sUserName = usr.UserName;
			    	String sUserPhone = Util.convGlobalPhoneNumber(usr.UserPhone) ;
				    	
					if (userNames.equals(""))
						userNames = sUserName ;
					else
						userNames += ","+sUserName ;
					
					if (userPhones.equals(""))
						userPhones = sUserPhone ;
					else
						userPhones += ","+sUserPhone ;
					nMember++;

					if (nMember >= MAX)
					{
						nMember = 0 ;
						ret = ReqParser.reqSubMember(Const.curGroupInfo.GroupSid, userNames, userPhones) ;
						if (ret > 0) {
							nMember = 0 ;
							break;
						}
						userNames = "" ;
						userPhones = "" ;
					}
				}

				if (nMember > 0)
				   ret = ReqParser.reqSubMember(Const.curGroupInfo.GroupSid, userNames, userPhones) ;
				return ret;
			} 
			catch(Exception e) 
			{
				return 99;
			}
		}
		
		
		@Override
		protected void onPostExecute(Integer result) 
		{
			if (result == 1) // error
			{
				// failed
			}
			else 
			{
		        reqMemberList("");
			}
			if (progress.isShowing())
			{
				try { progress.dismiss(); } catch(Exception e) { e.printStackTrace(); }
			}
		}
		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(MemberList.this);
			progress.setMessage(getString(R.string.member_registering));
			progress.setCancelable(true);
			progress.show();

			super.onPreExecute();
		}

	}

	@Override
	protected void onResume( )
	{
		super.onResume( );
	}
	
	public void reqMemberList(String search_str)
	{
		new ReqMemberListTask().execute(search_str);
	}

	private class ReqMemberListTask extends AsyncTask<String, Integer, Integer> 
	{

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				boolean bTimeOut = false ;
	    		long time = Util.getCurTime() ;
	    		while ((!bTimeOut) && (Const.isReqProcess))
	    		{
	    			if (Util.getCurTime() - time >= Const.TimeOutGetGroupInfos) 
	    				bTimeOut = true ;
	    			else
	    			    Util.sleep(1);
	    		}
	    		
	    		if (bTimeOut)
	    			return 99 ;
	    		
	    		Const.isReqProcess = true ;
				int n = ReqParser.reqMemberList(Const.curGroupInfo.GroupSid, 50, strData[0]) ;
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
			if (result == 0) // reqSendInfo success
			{
				refreshList() ;
			}
			Const.isReqProcess = false ;
		}
	}

	
    @Override
    public void onDestroy() 
    {
        super.onDestroy();
    }
    
    public void delete_group_pressed(View v)
    {
		new AlertDialog.Builder(this, R.style.AlertWhite).setMessage(R.string.group_delete_ask) 
		.setCancelable(false)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				deleteCurGroup();
			}
		})
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// no
			}
		})
		.show();
    }
    
	public void makeFromPhoneContacts() 
	{
        // Do something in response to the click
		
		Intent intent = new Intent(this, ContactsList.class);
        
		startActivityForResult(intent, PICK_CONTACT);
    }
    
    public void member_regist_pressed(View v)
    {
		new AlertDialog.Builder(this, R.style.AlertWhite).setMessage(R.string.member_regist_help) 
		.setCancelable(true)
		.setPositiveButton(R.string.member_regist_help3, new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// from phone contacts
				makeFromPhoneContacts() ;
			}
		})
		.setNeutralButton(R.string.member_regist_help1, new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// file 입력
				makeMemberFile() ;
			}
		})
		.setNegativeButton(R.string.member_regist_help2, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// 수동 입력
				makeMemberManual() ;
			}
		})
		.show();
    }

	private void makeMemberFile() 
    {
		new AlertDialog.Builder(this).setMessage(R.string.member_make_file_help) 
		.setCancelable(true)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
		    	// choose file
		        Intent chooseFile;
		        Intent intent;
		        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
		        chooseFile.setType("file/*");
		        intent = Intent.createChooser(chooseFile, "Choose a file");
		        intent.setFlags(intent.FLAG_GRANT_READ_URI_PERMISSION);
		        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// no
			}
		})
		.show();
		
    }
    
	public void btn_ok_pressed(View v) 
	{
		// member regist
		member_no = Util.convGlobalPhoneNumber(et_member_no.getText().toString().trim());
		member_nm = et_member_nm.getText().toString().trim();
		if ((member_no.length() < 10) || (member_nm.length() < 2))
			Toast.makeText(this, strR(R.string.member_make_wrong), Toast.LENGTH_SHORT);
		else
		{
			new MemberRegTask().execute(member_nm, member_no) ;
		}
	}

	private class MemberRegTask extends AsyncTask<String, Integer, Integer> 
	{

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				int n = ReqParser.reqSubMember(Const.curGroupInfo.GroupSid, strData[0], strData[1]) ;
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
				Toast.makeText(MemberList.this, strR(R.string.member_sub_success), Toast.LENGTH_SHORT);
				et_member_no.setText("");
				et_member_nm.setText("");
				MemberInfo info = new MemberInfo() ;
				info.Name = member_nm ;
				info.Phone = Util.convLocalPhoneNumber(member_no) ;
				Const.curGroupInfo.memberList.add(info);
				refreshList();
			}
		}
	}
	
    
    private void makeMemberManual()
    {
	// DSP dialog
	mMemberMakeDialog = new MemberMakeDialog();

	mMemberMakeDialog.show(getFragmentManager(), "MYTAG");
    }
    
	public static class MemberMakeDialog extends DialogFragment {

		public String strR(int id)
		{
			return(getResources().getString(id));
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(
					getActivity());
			LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
			View v = mLayoutInflater
					.inflate(R.layout.member_make_dialog, null) ;
			
			mBuilder.setView(v);
			mBuilder.setTitle(strR(R.string.member_make));
			mBuilder.setMessage(strR(R.string.member_make_help));
			et_member_no = (EditText) v.findViewById(R.id.et_member_no) ;
			et_member_nm = (EditText) v.findViewById(R.id.et_member_nm) ;
			
			return mBuilder.create();
		}
		
		
		@Override
		public void onStop() {
			mMemberMakeDialog = null ;
			super.onStop();
		}

	}
    

	public void deleteCurGroup()
    {
    	new DeleteGroupTask().execute();
    }
    
	private class DeleteGroupTask extends AsyncTask<String, Integer, Integer> 
	{

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				int n = ReqParser.reqDeleteGroup(Const.curGroupInfo.GroupSid) ;
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
				Const.curGroupInfo = null ;
				setResult(RESULT_OK);
				finish() ;
			}
		}
	}
    
	public void btn_search_pressed(View v) 
	{
        // Do something in response to the click
		String s = et_search.getText().toString().trim() ;
		reqMemberList(s);
    }
    
	public void refreshList()
	{
        lv.invalidateViews();
	}
	
	public void reqDeleteMember(int uSid)
	{
		new ReqDeleteMemberTask().execute(uSid+"");
	}

	public void reqUseOX(int uSid, boolean bUse)
	{
		new ReqUseOXTask().execute(uSid+"", bUse?"1":"0");
	}

	private class ReqUseOXTask extends AsyncTask<String, Integer, Integer> 
	{

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				int n = ReqParser.reqUseOX(Const.curGroupInfo.GroupSid, Integer.parseInt(strData[0]), strData[1]) ;
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
				refreshList() ;
			}
		}
	}
	
	private class ReqDeleteMemberTask extends AsyncTask<String, Integer, Integer> 
	{

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				int n = ReqParser.reqDeleteMember(Const.curGroupInfo.GroupSid, Integer.parseInt(strData[0])) ;
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
				Const.curGroupInfo.memberList.remove(askInfo);
				refreshList() ;
			}
		}
	}
	
    protected void selectMember(int ix)
    {
    	// change use or not use
    	if ((Const.curGroupInfo.memberList == null) || (ix >= Const.curGroupInfo.memberList.size()))
    	    return ;
    	
    	MemberInfo info = Const.curGroupInfo.memberList.get(ix) ;
        if (info.MemberSid != Const.getMySid() && Const.curGroupInfo.isGroupMgr())
        	askUseDeleteOrNot(info) ;
    }
    /*
    <string name="member_op_ask">이 멤버에 대한 동작은?</string>
    <string name="mamber_op_delete">이 멤버에 대한 내용 삭제</string>
    <string name="mamber_op_use_O">이 멤버에 대해 암호화/해독 가능케 함.</string>
    <string name="mamber_op_use_X">이 멤버에 대해 암호화/해독 불가능케 함.</string>
    */
    private void askUseDeleteOrNot(MemberInfo info)
    {
    	askInfo = info ;
		new AlertDialog.Builder(this).setMessage(R.string.member_op_ask) 
		.setCancelable(true)
		.setPositiveButton((info.UseYN==1)?R.string.member_op_use_X:R.string.member_op_use_O, new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// yes
				if (askInfo.UseYN==1)
					reqUseOX(askInfo.MemberSid, false);
				else
					reqUseOX(askInfo.MemberSid, true);
			}
		})
		.setNeutralButton(R.string.member_op_delete, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				askDeleteMember(askInfo.Name, askInfo.MemberSid);
				//reqDeleteMember(askInfo.MemberSid);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// no
			}
		})
		.show();

    }
    
    
	private void askDeleteMember(String name, int uSid)
	{
		String str = String.format(strR(R.string.member_delete_ask), name);
		new AlertDialog.Builder(this).setMessage(str) 
		.setCancelable(true)
		.setPositiveButton(strR(R.string.yes), new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// yes
				reqDeleteMember(askInfo.MemberSid);
			}
		})
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// no
			}
		})
		.show();
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        v.setBackgroundColor(Color.CYAN);        
    	selectMember(position);
    }

    public class MemberListAdapter extends BaseAdapter {
        public MemberListAdapter(Context c) {
            mContext = c;
        }


        @Override
        public int getCount() {
            return ((Const.curGroupInfo.memberList==null)?0:Const.curGroupInfo.memberList.size());
        }

        @Override
        public Object getItem(int position) {
        	if (position == 0)
        		return null ;
        	else
               return Const.curGroupInfo.memberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            ViewHolder viewHolder;
            
            if (convertView == null)
            {
                Const.MyLog("Const.curGroupInfo.memberList", "convertView = null");
                LayoutInflater vi = LayoutInflater.from(this.mContext);  
                convertView = vi.inflate(R.layout.member_item, null);
                
                viewHolder = new ViewHolder();
                
                viewHolder.position = position ;
                viewHolder.ll_member = (LinearLayout)convertView.findViewById(R.id.ll_member);
                viewHolder.ll_member.setTag(viewHolder);
                viewHolder.ll_member.setOnClickListener(new View.OnClickListener() 
                {
        			@Override
        			public void onClick(View v) 
        			{
        		        v.setBackgroundColor(Color.MAGENTA);        
        				ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;

                        selectMember(ix);
        			}
        		});

                
//                ActivitySwipeDetector swipe = new ActivitySwipeDetector(this);
//                viewHolder.ll_message.setOnTouchListener(swipe);
                
                viewHolder.tv_member_name = (TextView)convertView.findViewById(R.id.tv_member_name);
                viewHolder.tv_member_name.setTag(viewHolder);
                viewHolder.tv_member_name.setOnClickListener(new View.OnClickListener() {
        			
        			@Override
        			public void onClick(View v) 
        			{
                        ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;
                        selectMember(ix);
        			}
        		});
                
                convertView.setTag(viewHolder); 
                
                viewHolder.tv_member_state = (TextView)convertView.findViewById(R.id.tv_member_state);
                viewHolder.tv_member_state.setTag(viewHolder);
                viewHolder.tv_member_state.setOnClickListener(new View.OnClickListener() {
        			
        			@Override
        			public void onClick(View v) 
        			{
                        ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;
                        selectMember(ix);
        			}
        		});
            }
            else 
            {
                //Get view holder back
            	viewHolder = (ViewHolder) convertView.getTag();
                Const.MyLog("MemberlList", "convertView != null");
            }
            
            viewHolder.position = position ;
            
            MemberInfo info = null ;
            if (!Const.curGroupInfo.memberList.isEmpty() && (position < Const.curGroupInfo.memberList.size()))
            	info = Const.curGroupInfo.memberList.get(position);
            
            if (info != null) 
                {
                   viewHolder.ll_member = (LinearLayout)convertView.findViewById(R.id.ll_member);
                   viewHolder.ll_member.setBackgroundColor(Color.TRANSPARENT);

               	   //name
                   viewHolder.tv_member_name = (TextView) convertView.findViewById(R.id.tv_member_name);
                   String str1 = info.Name ;
                   if (Const.curGroupInfo.isGroupMgr())
                	   str1 += "("+info.Phone+")" ;
                   
               	   viewHolder.tv_member_name.setText(str1);

                   //state
               	   String str ;
               	   if (info.UserState == 0)
               		   str = strR(R.string.member_state_0);
               	   else if (info.UserState == 1)
               		   str = strR(R.string.member_state_1);
               	   else
               		   str = strR(R.string.member_state_2);
               		   
                   viewHolder.tv_member_state = (TextView) convertView.findViewById(R.id.tv_member_state);
            	   viewHolder.tv_member_state.setText(str);
            	   
            	   //use
                   viewHolder.tv_use = (TextView) convertView.findViewById(R.id.tv_use);
                   String str2 = "" ;
                   if (info.UseYN == 0)
                	   str2 = strR(R.string.use_limit);
                   else
                	   str2 = strR(R.string.use_allow);
            	   viewHolder.tv_use.setText(str2);
                }
            return convertView;
        }
        private Context mContext;

    }
    
    public static class ViewHolder 
    {
    	int position ;
    	LinearLayout ll_member ;
    	TextView tv_member_name;
        TextView tv_member_state;
        TextView tv_use ;
    }

}