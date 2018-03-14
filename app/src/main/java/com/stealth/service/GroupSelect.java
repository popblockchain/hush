package com.stealth.service;

import com.stealth.service.GroupSelect.GroupSelectAdapter;
import com.stealth.service.GroupSelect.ViewHolder;
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

public class GroupSelect extends ListActivity 
{
	ListView lv ;
	
	int target_ix ;
	boolean bReady = false ; // don't display list before ready
	int nFromCopy = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_select);
        
        Const.groupSelect = this ;

		if (getIntent().getBooleanExtra("FromCopy", false) == false)
			nFromCopy = 0 ;

		if (nFromCopy == 0)
		{
		}

        GroupSelectAdapter cAdapter = new GroupSelectAdapter(this); 
        setListAdapter(cAdapter);
        
        lv = (ListView)findViewById(android.R.id.list);

        lv.setOnItemClickListener(null);
        
        reqGroupSelect() ;
    }
    

    public String strR(int id)
    {
    	return(getResources().getString(id));
    }
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
	}
	
	public void reqGroupSelect()
	{
		Const.service1.getGroupInfos(0) ;
	}
	
    @Override
    public void onDestroy() 
    {
    	Const.groupSelect = null ;
    	
        super.onDestroy();
    }

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data)
	{
		switch (reqCode)
		{
			case 0 :
					finish() ;
				break ;
		}
	}



	public void refreshList()
	{
		bReady = true ;
        lv.invalidateViews();
	}

    private int getCount() 
    {
		if (!bReady)
			return 0 ;

    	if (Const.groupArrs == null)
    		return 1+nFromCopy ;
    	int cnt = 0 ;
    	for (GroupInfo info : Const.groupArrs)
    	{
    		if (info.isUseOK() || info.isGroupMgr())
    			cnt++;
    	}
    	return (cnt+1+nFromCopy) ;
    }

	private GroupInfo getGroup(int position)
	{
		if (!bReady || Const.groupArrs == null)
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
    
    protected void selectGroup(int ix)
    {
    	int cnt = getCount();

    	if (ix >= cnt)
    	    return ;
    	
    	if (ix < cnt-1-nFromCopy)
    	{
        	GroupInfo info = getGroup(ix) ;
        	Const.setCurGroupInfo(info) ;

//        	Const.saveCurGroup(info.GroupSid, info.GroupId);
    	}
    	else
    	{
    		Const.setCurGroupInfo(null) ;
			setResult(RESULT_OK);
			finish() ;
//        	Const.saveCurGroup(0, "");
    	}
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        v.setBackgroundColor(Color.CYAN);        
    	selectGroup(position);
    }

    public class GroupSelectAdapter extends BaseAdapter {
        public GroupSelectAdapter(Context c) {
            mContext = c;
        }


        @Override
        public int getCount() {
			if (!bReady)
				return 0 ;

        	if (Const.groupArrs == null)
        		return 1+nFromCopy ;
        	int cnt = 0 ;
        	for (GroupInfo info : Const.groupArrs)
        	{
        		if (info.isUseOK())
        			cnt++;
        	}
        	return cnt+1+nFromCopy ;
        }

        @Override
        public Object getItem(int position) 
        {
			if (!bReady)
				return null ;
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
                Const.MyLog("Const.groupArrs", "convertView = null");
                LayoutInflater vi = LayoutInflater.from(this.mContext);  
                convertView = vi.inflate(R.layout.group_select_item, null);
                
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
						v.setBackgroundColor(Color.MAGENTA);
						ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;
                        selectGroup(ix);
        			}
        		});
                
                convertView.setTag(viewHolder); 
            }
            else 
            {
                //Get view holder back
            	viewHolder = (ViewHolder) convertView.getTag();
                Const.MyLog("GrouplList", "convertView != null");
            }
            
            viewHolder.position = position ;
            viewHolder.ll_group = (LinearLayout)convertView.findViewById(R.id.ll_group);
            viewHolder.tv_group_id = (TextView) convertView.findViewById(R.id.tv_group_id);
            viewHolder.ll_group.setBackgroundColor(Color.TRANSPARENT);

            
            if (position == getCount()-1-nFromCopy) // Common Key
            {
            	   viewHolder.tv_group_id.setText(getResources().getString(R.string.group_no));
				   viewHolder.ll_group.setBackgroundColor(0xff80ff80); // green
                return convertView;
            }
			else if (position == getCount()-1) // user Key
			{
				viewHolder.tv_group_id.setText(getResources().getString(R.string.user_key_detail));
				viewHolder.ll_group.setBackgroundColor(0xffffc0ff); // violet
				return convertView;
			}
            else
            {
                viewHolder.ll_group.setBackgroundColor(0xff80ffff); // sky blue
            }

            GroupInfo info = (GroupInfo)getItem(position); //Const.groupArrs.get(position);
            //GroupInfo info = Const.groupArrs.get(position);
            
            if (info != null) 
                {
               	   //id
               	   viewHolder.tv_group_id.setText(info.GroupId);
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
    }

}