package com.stealth.service;

import com.stealth.hushkbd.R;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class DetailList extends ListActivity 
{
	TextView tv_ok ;
	ListView lv_list ;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_list);

        setTitle1(); // ()�� �̱׷��� ���õ� ����ڼ�
        
        DetailListAdapter cAdapter = new DetailListAdapter(this); 
        setListAdapter(cAdapter);
        
        lv_list = (ListView)findViewById(android.R.id.list);
//        ListView lv = (ListView)findViewById(R.id.list_detail);
        lv_list.setOnItemClickListener(null);
    }


	protected void refreshList()
	{
        lv_list.invalidateViews();
	}
	
	protected void setTitle1()
    {
    	String str = getResources().getString(R.string.detail_list);
    	String result = String.format(str, Const.getGroupList().get(Const.curGroupIx).GroupName);
        setTitle(result+"("+Const.getSelectCount()+")");
    }
    
    protected void changeListSelect(int ix, boolean bChecked)
    {
    	int lineAll = 1;
    	if (!Const.searchWord.equals(""))
    		lineAll = 0 ;

    	if (ix < lineAll)
       {
   	      Const.changeSelectAll(bChecked);
       }
       else
       {
 	      Const.changeSelectUser(ix-lineAll, bChecked);
       }
       
       lv_list.invalidateViews();
       setTitle1();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
    	Const.ContactUser usr = Const.getGroupList().get(Const.curGroupIx).getContacts().get(position) ;
    	if (usr.bSelected)
    		usr.bSelected = false ;
    	else
    		usr.bSelected = true ;
    	
        lv_list.invalidateViews();
    }

    public class DetailListAdapter extends BaseAdapter{
        public DetailListAdapter(Context c) {
            mContext = c;
        }
        @Override
        public int getCount() 
        {
            return (Const.getGroupList().get(Const.curGroupIx).getContacts().size()+(Const.searchWord.equals("")?1:0));
        }

        @Override
        public Object getItem(int position) 
        {
        	int lineAll = 1;
        	if (!Const.searchWord.equals(""))
        		lineAll = 0 ;
        	
        	if (position < lineAll)
        		return null ;
        	else
               return Const.getGroupList().get(Const.curGroupIx).getContacts().get(position-lineAll);
        }

        @Override
        public long getItemId(int position) 
        {
        	int lineAll = 1;
        	if (!Const.searchWord.equals(""))
        		lineAll = 0 ;
            return (position-lineAll);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            ViewHolder viewHolder;
            
            if (convertView == null)
            {
                Const.MyLog("DetailList", "convertView = null"); //mylog
                LayoutInflater vi = LayoutInflater.from(this.mContext);  
                convertView = vi.inflate(R.layout.detail_list_item, null);
                viewHolder = new ViewHolder();
                
                viewHolder.position = position ;
                viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check_user);
                viewHolder.textuser = (TextView)convertView.findViewById(R.id.text_user);
                viewHolder.textphone = (TextView)convertView.findViewById(R.id.text_phone);
                viewHolder.checkbox.setTag(viewHolder);
                
                viewHolder.checkbox.setOnClickListener(new View.OnClickListener() 
                {
        			@Override
        			public void onClick(View v) 
        			{
        				ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;

                        changeListSelect(ix, viewHolder.checkbox.isChecked());
                   
                        setTitle1();
        			}
        		});
                /*
                viewHolder.checkbox
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton buttonView,
                                    boolean isChecked) 
                            {
                            	if (!Const.bInGetView)
                            	{
                                    ViewHolder viewHolder = (ViewHolder)buttonView.getTag() ;
                                    int ix = viewHolder.position ;

                                    changeListSelect(ix, isChecked);
                               
                                    setTitle1();
                            	}
                            }
                        });
                */
                convertView.setTag(viewHolder); 
                
                convertView.findViewById(R.id.select_user).setTag(viewHolder);
                convertView.findViewById(R.id.select_user).setOnClickListener(new View.OnClickListener() {
        			
        			@Override
        			public void onClick(View v) 
        			{
                        ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position ;
                        CheckBox cb = viewHolder.checkbox ;
                        changeListSelect(ix, !cb.isChecked());
        			}
        		});
            }
            else 
            {
                //Get view holder back
            	viewHolder = (ViewHolder) convertView.getTag();
                Const.MyLog("DetailList", "convertView != null"); //mylog
            }
            
            viewHolder.position = position ;
        	int lineAll = 1;
        	if (!Const.searchWord.equals(""))
        		lineAll = 0 ;
            
            if (position < lineAll) // ��ü ���� row
            {
            	Const.ContactGroup g = Const.getGroupList().get(Const.curGroupIx);
         	    viewHolder.checkbox.setChecked(g.bSelectAllUsers);

         	    // "Select All"
                viewHolder.textuser = (TextView) convertView.findViewById(R.id.text_user);
         	    viewHolder.textuser.setText(getResources().getString(R.string.select_all));
                viewHolder.textphone = (TextView) convertView.findViewById(R.id.text_phone);
         	    viewHolder.textphone.setText("");
            }
            else
            {
                Const.ContactUser c = Const.getGroupList().get(Const.curGroupIx).getContacts().get(position-lineAll);
            
                if (c != null) 
                {
            	   // check box
                   viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check_user);
            	   viewHolder.checkbox.setChecked(c.bSelected);
            	
                   //Name
                   viewHolder.textuser = (TextView) convertView.findViewById(R.id.text_user);
            	   viewHolder.textuser.setText(c.UserName);

            	   //Phone
                   viewHolder.textphone = (TextView) convertView.findViewById(R.id.text_phone);
            	   viewHolder.textphone.setText(c.UserPhone);
                }
            }
            
            Const.MyLog("DetailList", "getViewRow="+position); //mylog

            return convertView;
        }
        private Context mContext;

    }
    
    public static class ViewHolder 
    {
    	int position ;
    	CheckBox checkbox;
        TextView textuser;
        TextView textphone;
    }

}