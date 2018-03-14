package com.stealth.service;

import com.stealth.service.GroupList.GroupListAdapter;
import com.stealth.service.GroupList.ViewHolder;
import com.stealth.hushkbd.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GroupList extends ListActivity 
{
//	Button btn_recent ;
//	Button btn_saved ;
	public static final int  GROUP_REG_RESULT = 20 ;
	public static final int  GROUP_MODIFY_RESULT = 25 ;
	public static final int  MEMBER_LIST_RESULT = 30 ;
	ListView lv ;
	
	int target_ix ;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list);
        Const.groupList = this ;
        
        GroupListAdapter cAdapter = new GroupListAdapter(this); 
        setListAdapter(cAdapter);
        
        lv = (ListView)findViewById(android.R.id.list);

        lv.setOnItemClickListener(null);
        
        if (!Const.isReqProcess)
        {
            Const.service1.getGroupInfos(0) ;
        }
    }
    
    public void onBackPressed()
    {
    	if (Const.isReqProcess)
    		return ;
    	
    	super.onBackPressed(); 
    	/*
    	 FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) ll_main.getLayoutParams();
         lp2.width = 300;
         lp2.height =400;
         ll_main.setLayoutParams(lp2);
         */

    	//ll_main.setVisibility(View.INVISIBLE);
    }

	public void btn_regist_pressed(View v) 
	{
		if (!Const.checkRegGroup(this))
			return ;

		Const.groupRegMode = Const.GRM_Regist ;
		Intent intent = new Intent(GroupList.this, GroupReg.class);
	    
		startActivityForResult(intent, GROUP_REG_RESULT);
    }
    
    public String strR(int id)
    {
    	return(getResources().getString(id));
    }
    
	
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) 
	{
	  switch (reqCode) 
	  {
	  case GROUP_REG_RESULT : 
	  case GROUP_MODIFY_RESULT :
		  refreshList() ;
		  break ;
	  case MEMBER_LIST_RESULT :
		  refreshList() ;
		  break ;
	  }
	}


	@Override
	protected void onResume( )
	{
		super.onResume( );
        //Const.checkGroupInvitation(this);
	}
	

	
    @Override
    public void onDestroy() 
    {
        Const.groupList = null ;

        super.onDestroy();
    }
    
    
	public void refreshList()
	{
        lv.invalidateViews();
	}

	private GroupInfo getGroup(int position)
	{
		if (Const.groupArrs == null)
			return null ;
		else
		{
			int nValid = 0 ;
	    	for (GroupInfo info : Const.groupArrs)
			{
				if (info.isUseOK())
				{
					if (position == nValid)
						return info ;
					nValid++ ;
				}
			}
	    	return null ;
		}
	}

	
    protected void selectGroup(int ix)
    {
    	GroupInfo info = getGroup(ix) ;
    	if (info == null)
    		return ;
    	
    	Const.setCurGroupInfo(info) ;
    	
//    	Const.saveCurGroup(info.GroupSid, info.GroupId);
    	
    	// if the user id group mgr -> goto GroupReg
    	// else
    	//    if the group allow member list view -> got memberList
    	//    else -> no action
    	if (info.isGroupMgr())
    	{
    		Const.groupRegMode = Const.GRM_Modify ;
    		Intent intent = new Intent(GroupList.this, GroupReg.class);
    		startActivityForResult(intent, GROUP_MODIFY_RESULT);
    	}
    	else
    	{
    		if (info.MemberViewYN == 1)
    		{
    			Intent intent = new Intent(this, MemberList.class);
    	        
    			startActivityForResult(intent, MEMBER_LIST_RESULT);
    		}
    	}
    }

    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        v.setBackgroundColor(Color.CYAN);        
    	selectGroup(position);
    }

    public class GroupListAdapter extends BaseAdapter {
        public GroupListAdapter(Context c) {
            mContext = c;
        }


        @Override
        public int getCount() 
        {
        	if (Const.groupArrs == null)
        		return 0 ;
        	int cnt = 0 ;
        	for (GroupInfo info : Const.groupArrs)
        	{
        		if (info.isUseOK() || info.isGroupMgr())
        			cnt++;
        	}
        	return cnt ;
        }

        @Override
        public Object getItem(int position) 
        {
        	if (Const.groupArrs == null)
        		return null ;
        	else
        	{
        		int nValid = 0 ;
            	for (GroupInfo info : Const.groupArrs)
        		{
        			if (info.isUseOK() || info.isGroupMgr())
        			{
        				if (position == nValid)
        					return info ;
        				nValid++ ;
        			}
        		}
            	return null ;
        	}
        }

        @Override
        public long getItemId(int position) 
        {
            return (position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            ViewHolder viewHolder;
            
            if (convertView == null)
            {
                Const.MyLog("Const.groupList", "convertView = null");
                LayoutInflater vi = LayoutInflater.from(this.mContext);  
                convertView = vi.inflate(R.layout.group_item, null);
                
                viewHolder = new ViewHolder();
                
                viewHolder.position = position ;
                viewHolder.ll_group = (LinearLayout)convertView.findViewById(R.id.ll_group);
                viewHolder.ll_group.setTag(viewHolder);
                viewHolder.ll_group.setOnClickListener(new View.OnClickListener() 
                {
        			@Override
        			public void onClick(View v) 
        			{
        		        v.setBackgroundColor(Color.MAGENTA);        
        				ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;

                        selectGroup(ix);
        			}
        		});

                
//                ActivitySwipeDetector swipe = new ActivitySwipeDetector(this);
//                viewHolder.ll_message.setOnTouchListener(swipe);
                
                viewHolder.tv_group_id = (TextView)convertView.findViewById(R.id.tv_group_id);
                viewHolder.tv_group_id.setTag(viewHolder);
                viewHolder.tv_group_id.setOnClickListener(new View.OnClickListener() {
        			
        			@Override
        			public void onClick(View v) 
        			{
                        ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;
                        selectGroup(ix);
        			}
        		});
                
                convertView.setTag(viewHolder); 
                
                viewHolder.tv_mgr_name = (TextView)convertView.findViewById(R.id.tv_mgr_name);
                viewHolder.tv_mgr_name.setTag(viewHolder);
                viewHolder.tv_mgr_name.setOnClickListener(new View.OnClickListener() {
        			
        			@Override
        			public void onClick(View v) 
        			{
                        ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;
                        selectGroup(ix);
        			}
        		});
            }
            else 
            {
                //Get view holder back
            	viewHolder = (ViewHolder) convertView.getTag();
                Const.MyLog("GrouplList", "convertView != null");
            }
            
            viewHolder.position = position ;
            
            GroupInfo info = (GroupInfo)getItem(position); //Const.groupArrs.get(position);
            
            if (info != null) 
                {
                   viewHolder.ll_group = (LinearLayout)convertView.findViewById(R.id.ll_group);
                   viewHolder.ll_group.setBackgroundColor(Color.TRANSPARENT);

               	   //id
                   viewHolder.tv_group_id = (TextView) convertView.findViewById(R.id.tv_group_id);
               	   viewHolder.tv_group_id.setText(info.GroupId);

                   //member수
                   viewHolder.tv_member_cnt = (TextView) convertView.findViewById(R.id.tv_member_cnt);
            	   viewHolder.tv_member_cnt.setText(info.MemberCnt+"");
            	   
            	   //del
                   viewHolder.tv_mgr_name = (TextView) convertView.findViewById(R.id.tv_mgr_name);
            	   viewHolder.tv_mgr_name.setText(info.MgrName);
                }
            return convertView;
        }
        private Context mContext;

    }
    
    public static class ViewHolder 
    {
    	int position ;
    	LinearLayout ll_group ;
    	TextView tv_group_id;
        TextView tv_member_cnt;
        TextView tv_mgr_name ;
    }

}