package com.stealth.service;

import com.stealth.hushkbd.R;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class GroupReg extends Activity 
{
	LinearLayout ll_user_name ;
    Button   btn_group_regist ;
    Button   btn_member_list ;

    EditText et_group_id ;
    EditText et_user_name ;
    
    RadioButton rbtn_member_view_all ;
    RadioButton rbtn_member_view_mgr ;

    RadioButton rbtn_decrypt_copy_yes ;
    RadioButton rbtn_decrypt_copy_no ;

    RadioButton rbtn_otg_must_yes ;
    RadioButton rbtn_otg_must_no ;
    
    TextView tv_otg_use ;

    String sUserName = "" ;
    String sGroupId = "" ;
    
    int regist_result ;
    
    int member_view = 1 ;
    
    int decrypt_copy = 1 ;
    
    int otg_must = 0 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		Const.join = this ;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      
        
		setContentView(R.layout.group_reg);

		ll_user_name = (LinearLayout)findViewById(R.id.ll_user_name);
		
		et_group_id = (EditText)findViewById(R.id.et_group_id);
		et_user_name = (EditText)findViewById(R.id.et_user_name);
		
        rbtn_member_view_all = (RadioButton)findViewById(R.id.rbtn_member_view_all);
        rbtn_member_view_mgr = (RadioButton)findViewById(R.id.rbtn_member_view_mgr);

        rbtn_decrypt_copy_yes = (RadioButton)findViewById(R.id.rbtn_decrypt_copy_yes);
        rbtn_decrypt_copy_no = (RadioButton)findViewById(R.id.rbtn_decrypt_copy_no);

        rbtn_otg_must_yes = (RadioButton)findViewById(R.id.rbtn_otg_must_yes);
        rbtn_otg_must_no = (RadioButton)findViewById(R.id.rbtn_otg_must_no);

        btn_group_regist = (Button)findViewById(R.id.btn_group_regist);
        btn_member_list = (Button)findViewById(R.id.btn_member_list);
        
        tv_otg_use = (TextView)findViewById(R.id.tv_otg_use);

			tv_otg_use.setVisibility(View.INVISIBLE);
			rbtn_otg_must_yes.setVisibility(View.INVISIBLE);
			rbtn_otg_must_no.setVisibility(View.INVISIBLE);

        if (Const.groupRegMode == Const.GRM_Modify)
		{
			setValues(Const.curGroupInfo);
		}
		else
		    dspBtns() ;
		
		if (!Const.getMyName().equals(""))
		{
			ll_user_name.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) 
	{
	  switch (reqCode) 
	    {
	    case 100 :
	    	//dspRbtns();
	    	if (Const.curGroupInfo == null)
	    		finish() ;
	        break ;
	    }
	}
	
	
	private void setValues(GroupInfo info)
	{
		sUserName = info.MgrName ;
		sGroupId = info.GroupId ;
		member_view = info.MemberViewYN ;
		decrypt_copy = info.DecryptCopyYN ;
		otg_must = info.SdcardMustYN ;
		et_user_name.setText(sUserName);
		et_group_id.setText(sGroupId);
		dspBtns() ;
	}
	
	private void dspBtns()
	{
	    if (Const.groupRegMode == Const.GRM_Regist)
	    {
	    	btn_member_list.setVisibility(View.INVISIBLE);
	    	btn_group_regist.setText(getResources().getString(R.string.group_regist));
	    	setTitle(getResources().getString(R.string.group_regist)+"("+getRecomStr()+")");
	    }
	    else
	    {
	    	btn_group_regist.setText(getResources().getString(R.string.group_modify));
	    	setTitle(getResources().getString(R.string.group_modify)+"("+getRecomStr()+")");
	    }
	}
	
	private String getRecomStr()
	{
		return "";
	}
	
    @Override
    protected void onResume() 
    {
        super.onResume();
        
    	if ((Const.curGroupInfo == null) && (Const.groupRegMode == Const.GRM_Modify))
    		finish() ;
		else
			dspRbtns();
    }

	public void rbtn_member_view_all_pressed(View v)
	{
		member_view = 1 ;
		dspRbtns() ;
	}
	
	public void rbtn_member_view_mgr_pressed(View v)
	{
		member_view = 0 ;
		dspRbtns() ;
	}
	
	public void rbtn_decrypt_copy_yes_pressed(View v)
	{
		decrypt_copy = 1 ;
		dspRbtns() ;
	}

	public void rbtn_decrypt_copy_no_pressed(View v)
	{
		decrypt_copy = 0 ;
		dspRbtns() ;
	}

	public void rbtn_otg_must_yes_pressed(View v)
	{
		otg_must = 1 ;
		dspRbtns() ;
	}

	public void rbtn_otg_must_no_pressed(View v)
	{
		otg_must = 0 ;
		dspRbtns() ;
	}

	
	private void dspRbtns() 
	{
		rbtn_member_view_all.setChecked(false);
		rbtn_member_view_mgr.setChecked(false);
		if (member_view == 1)
		{
			rbtn_member_view_all.setChecked(true);
		}
		else
		{
			rbtn_member_view_mgr.setChecked(true);
		}

		rbtn_decrypt_copy_yes.setChecked(false);
		rbtn_decrypt_copy_no.setChecked(false);
		if (decrypt_copy == 1)
		{
			rbtn_decrypt_copy_yes.setChecked(true);
		}
		else
		{
			rbtn_decrypt_copy_no.setChecked(true);
		}

		rbtn_otg_must_yes.setChecked(false);
		rbtn_otg_must_no.setChecked(false);
		if (otg_must == 1)
		{
			rbtn_otg_must_yes.setChecked(true);
		}
		else
		{
			rbtn_otg_must_no.setChecked(true);
		}
	}
	
	private String strR(int nid)
	{
		return (getResources().getString(nid)) ;
	}
	
	public void btn_member_list_pressed(View v)
	{
		gotoMemberList() ;
	}
	
	public void btn_group_regist_pressed(View v) 
	{
		if (Const.getMyName().equals(""))
		{
			sUserName = et_user_name.getText().toString().trim() ;
			if (sUserName.length() < 2)
			{
				dspRegistResult(16); 
				et_user_name.requestFocus() ;
				return ;
			}
		}
		
		sGroupId = et_group_id.getText().toString().trim() ;
		sGroupId = Util.removeSpaces(sGroupId);
		et_group_id.setText(sGroupId);

		if ((sGroupId.length() < 2) || (sGroupId.length() > 10) ||
			(!Util.isHangulAlphaNumeric(sGroupId)))
				
		{
			dspRegistResult(14); 
			et_group_id.requestFocus() ;
			return ;
		}
		
		askGroupRegistModify();
	}
	
	private boolean isGroupModified()
	{
		GroupInfo info = Const.curGroupInfo ;
		if (info.GroupId.equals(sGroupId) &&
			info.MgrName.equals(sUserName) &&
			info.MemberViewYN == member_view &&
			info.DecryptCopyYN == decrypt_copy &&
		    info.SdcardMustYN == otg_must)
			return false ;
		else
			return true ;
	}
	
	private void askGroupRegistModify()
	{
		String str ;
		if (Const.groupRegMode == Const.GRM_Regist)
		   str = strR(R.string.group_regist_ask);
		else
		{
			if (!isGroupModified())
			{
				return ;
			}
			else
		       str = strR(R.string.group_modify_ask);
		}
		
		new AlertDialog.Builder(this).setMessage(str) 
		.setCancelable(false)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// yes
		        new ReqRegModGroupTask().execute(sGroupId, sUserName, 
					     member_view+"", decrypt_copy+"", otg_must+"");
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
	
	private void gotoMemberList()
	{
		Intent intent = new Intent(GroupReg.this, MemberList.class);
		startActivityForResult(intent, 100);
	}
	
	private void askGotoMemberList()
	{
		String str = strR(R.string.member_regist_ask);
		
		new AlertDialog.Builder(this).setMessage(str) 
		.setCancelable(false)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// yes
		        gotoMemberList() ;
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

	private class ReqRegModGroupTask extends AsyncTask<String, Integer, Integer> 
	{

		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
			    int n = 0;
			    
			    if (Const.groupRegMode == Const.GRM_Regist)
			    {
  		            n = ReqParser.reqRegGroup1(strData[0], strData[1],
  		        		Integer.parseInt(strData[2]),Integer.parseInt(strData[3]),
  		        		Integer.parseInt(strData[4])) ;
			    }
			    else
			    {
  		            n = ReqParser.reqModifyGroup1(Const.curGroupInfo.GroupSid, strData[0], strData[1],
  		        		Integer.parseInt(strData[2]),Integer.parseInt(strData[3]),
  		        		Integer.parseInt(strData[4])) ;

					if (n==0)
					    ReqParser.reqGroupInfo1(Const.getCurGroupSid());

				}
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
		        //Const.service.regGroupKey(Const.getCurGroupSid());
		        setResult(RESULT_OK);
		        if (Const.groupRegMode == Const.GRM_Regist)
		        {
		        	Const.groupRegMode = Const.GRM_Modify ; //change to modify mode
		        	dspBtns() ;
		        	askGotoMemberList() ;
		        }
		        else
		        {
		        	finish() ;
		        }
			}
			else if (result == 1)
			{
				// same group already exist
				dspSameGroup() ;
			}
			else
				dspRegistResult(result);
				
		}
	}
	
	private void dspSameGroup() 
	{
	Toast.makeText(this, getResources().getString(R.string.group_exist), Toast.LENGTH_LONG).show();
	}

    private void dspRegistResult(int result)
    {
    	// 
    	// result = 0 if success
    	String msg ;
    	regist_result = result ;
    	
    	switch (result)
    	{
    	    case 0 : 
	    	       msg = getResources().getString(R.string.group_regist_success); 
    	        break ;
    	    case 14 : 
	    	       msg = getResources().getString(R.string.group_regist_idch_err); 
 	        break ;
    	    case 15 : 
	    	       msg = getResources().getString(R.string.group_regist_pwch_err); 
	        break ;
    	    case 16 : 
	    	       msg = getResources().getString(R.string.group_regist_user_name_err); 
	        break ;
	        default: msg = getResources().getString(R.string.group_regist_fail); 
    	}
         
   	    new AlertDialog.Builder(this).setMessage(msg)
   		.setCancelable(false)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
        {	
            @Override
            public void onClick(DialogInterface dialog, int which) 
            {
            	if (regist_result == 0)
            		finish() ;
            }
        })
        .show();
    }

	
}
