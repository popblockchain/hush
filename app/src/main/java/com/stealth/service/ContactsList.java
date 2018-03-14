package com.stealth.service;

import com.stealth.service.Const.ContactGroup;
import com.stealth.hushkbd.R;
import com.stealth.util.Util;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsList extends ListActivity 
{
//	TextView tv_refresh ;
	TextView tv_ok ;
	TextView tv_save ;
	TextView tv_title ;
	EditText et_search ;
	ListView lv ;
	String saveName ;
	String commonUse = "" ; // "1" or "0"
	String askGroupTitle ;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Const.searchWord = "" ;
        Const.searchContacts();
        
        Const.contactsList = this ;
        
        setContentView(R.layout.contacts_list);

        
        lv = (ListView)findViewById(android.R.id.list);
        lv.setOnItemClickListener(null);

//		tv_refresh = (TextView)findViewById(R.id.tv_refresh);
		tv_title = (TextView)findViewById(R.id.tv_title);
		et_search = (EditText)findViewById(R.id.et_search);

		ContactGroupAdapter cAdapter = new ContactGroupAdapter(this); 
        setListAdapter(cAdapter);
        
		/* get PhoneContacts in ContactsList */
		if (!Const.isMakingContacts)
		{
			if ((Const.getMakePhoneState() == 0)
			    || (Const.groups.isEmpty()))
	    	    reloadContacts();
		}
		
		
        et_search.addTextChangedListener(new TextWatcher() 
		{
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before,
	                int count) 
	        {
	            // TODO Auto-generated method stub
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) 
	        {
	            // TODO Auto-generated method stub
	        }

	        @Override
	        public void afterTextChanged(Editable s) 
	        {
	            // TODO Auto-generated method stub
	        	int count = 0 ;
	        	
	        	Const.searchWord = et_search.getText().toString().trim();
	        	Const.searchContacts();
	        	refreshList();
	        }
	    }
		);

        et_search.clearFocus();
//        hideKbd();
    }
    
    /*
    private void hideKbd()
    {
    	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
/*
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
*/        
    

	public void reloadContacts()
	{
		new ReloadContactsTask().execute();
	}
	
	private class ReloadContactsTask extends AsyncTask<String, Integer, Integer> 
	{
		ProgressDialog progress;
		ContactsDao contactsDao = new ContactsDao() ;
	
		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				long l1 = Util.getCurTime() ;
				contactsDao.deleteAllContacts();
				contactsDao.makePhoneContacts(); 

				long l2 = Util.getCurTime() ;
				if (l2 - l1 > 10000)
					Const.MyLog("makePhoneContacts", "time="+((l2-l1)/1000)+"secs");
				
				Const.MyLog("makePhoneContacts", "time="+(l2-l1)+"msecs");
				return(1); //return 1=success, 2=opt upgrade, 3=must upgrade
			} 
			catch(Exception e) 
			{
				return 99;
			}
		}
		
		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(ContactsList.this);
			progress.setMessage(getString(R.string.reloading));
			progress.setCancelable(false);
			progress.show();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) 
		{
			if (progress.isShowing()) 
			{ 
				try { progress.dismiss(); } catch(Exception e) { e.printStackTrace(); }
		    }
		
			if (Const.contactsList != null)
			   Const.contactsList.refreshList();
		    writePhoneContacts(); 
		}
	}
    
	public void writePhoneContacts()
	{
		new writeContactsTask().execute();
	}
	
	private class writeContactsTask extends AsyncTask<String, Integer, Integer> 
	{
		ProgressDialog progress;
		ContactsDao contactsDao = new ContactsDao() ;
		
		@Override
		protected Integer doInBackground(String... strData) 
		{
			try 
			{
				long l1 = Util.getCurTime() ;
			    // write to db
				    contactsDao.writeGroups();
				
			    for (ContactGroup grp1 : Const.groups)
			    {
			    	   contactsDao.writePhoneGroup(grp1);
			    }
			    
				long l2 = Util.getCurTime() ;
				if (l2 - l1 > 10000)
					Const.MyLog("writePhoneContacts", "time="+((l2-l1)/1000)+"secs");
				Const.MyLog("writePhoneContacts", "time="+(l2-l1)+"msecs");

				return(1); //return 1=success, 2=opt upgrade, 3=must upgrade
			} 
			catch(Exception e) 
			{
				return 99;
			}
		}
		
		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(ContactsList.this);
			progress.setMessage(getString(R.string.reloading));
			progress.setCancelable(false);
			progress.show();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) 
		{
			// this to after writePhoneTask
			if (result == 1)
			   Const.saveMakePhoneState(1);
			
			if (progress.isShowing()) 
			{ 
				try { progress.dismiss(); } catch(Exception e) { e.printStackTrace(); }
		    }
		
		}
	}


	public void btn_reload_pressed(View v) 
	{
		new AlertDialog.Builder(this).setMessage(R.string.reload_ask) 
		.setCancelable(false)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() 
		{	// ?
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// yes
				reloadContacts() ;
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
	
    
    
    
    private class GetNotAssignedContactsTask extends AsyncTask<String, Integer, Integer>
    {
		ProgressDialog progress;

		@Override
		protected Integer doInBackground(String... strData) 
		{
			while (Const.isMakingContacts)
			{
				SystemClock.sleep(100);
			}
		    return 1;
		}
		
		@Override
		protected void onPostExecute(Integer result) 
		{
			if (result == 1)
			{
				refreshList();
			}
			if (progress.isShowing()) 
			{ 
				try { progress.dismiss(); } catch(Exception e) { e.printStackTrace(); }
		    }
		}
		
		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(ContactsList.this);
			progress.setMessage(getString(R.string.gettingNotAssigned));
			progress.setCancelable(true);
			progress.show();

			super.onPreExecute();
		}
	}
    @Override
    protected void onResume() 
    {
        while (Const.isMakingContacts)
        {
        	Util.sleep(100);
        }
        
        super.onResume();

        Const.refreshSelected();

        // Start the simulation
        refreshList();
//        hideKbd();
    }

	public void refreshList()
	{
        setTitle1(Const.selectedContacts.size());
        lv.invalidateViews();
	}
	
	public void setTitle1(int nSelectCount)
    {
    	String str = getResources().getString(R.string.contacts_list);
    	
        str = str+"("+nSelectCount+")";
        tv_title.setText(str);
    }
    

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        Const.ContactGroup selectedGroup = 
              Const.groups.get(position);
        Toast.makeText(getBaseContext(), selectedGroup.GroupName + " ID #" + selectedGroup.GroupId, Toast.LENGTH_SHORT).show();
    }
    
    protected void gotoDetailList(int position)
	{
	    Const.curGroupIx = position ;
    
	    Const.ContactGroup c = 
            Const.groups.get(position);
    
        Intent intent = new Intent(ContactsList.this, DetailList.class);
        startActivity(intent); 
	}

    public class ContactGroupAdapter extends BaseAdapter{
        public ContactGroupAdapter(Context c) {
            mContext = c;
        }
        @Override
        public int getCount() {
        	
        	if (Const.isMakingContacts)
        		return 0 ;
        	else
               return Const.groups.size();
        }

        @Override
        public Object getItem(int position) {
            return Const.groups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            ViewHolder viewHolder;
            
            if(convertView == null)
            {
                LayoutInflater vi = LayoutInflater.from(this.mContext);  
                convertView = vi.inflate(R.layout.contacts_list_item, null);
                viewHolder = new ViewHolder();
                
                viewHolder.position = position ;
                viewHolder.select_group =(LinearLayout)convertView.findViewById(R.id.select_group);
                viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check_group);
                viewHolder.checkbox.setTag(viewHolder);
/*                viewHolder.checkbox
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton buttonView,
                                    boolean isChecked) 
                            {
                            if (!Const.bInGetView)
                               {
                               ViewHolder viewHolder = (ViewHolder)buttonView.getTag() ;
                               int ix = viewHolder.position;
                               Const.ContactGroup c = Const.groups.get(ix);
                               Const.SelectGroup(c, isChecked);
                               setTitle1(Const.contactsSelected.size());
                               }
                            }
                        });
*/                
                viewHolder.checkbox.setOnClickListener(new View.OnClickListener() 
                {
        			@Override
        			public void onClick(View v) 
        			{
        				if (Const.bGetGroupContacts) // not assigned group ������̸� 
        					return ;
        				ViewHolder viewHolder = (ViewHolder)v.getTag() ;
                        int ix = viewHolder.position;
                        Const.ContactGroup c = Const.groups.get(ix);
                        Const.SelectGroup(c, viewHolder.checkbox.isChecked());
                        refreshList();
                        setTitle1(Const.selectedContacts.size());
        			}
        		});

                convertView.setTag(viewHolder); 
                convertView.findViewById(R.id.select_group).setTag(viewHolder);
                convertView.findViewById(R.id.select_group).setOnClickListener(new View.OnClickListener() {
        			
        			@Override
        			public void onClick(View v) 
        			{
        				if (Const.bGetGroupContacts) // not assigned group ������̸� 
        					return ;
        		        v.setBackgroundColor(Color.CYAN);        
        				ViewHolder viewHolder = (ViewHolder)v.getTag() ;
        				gotoDetailList(viewHolder.position);
        			}
        		});
            }
            else 
            {
                //Get view holder back
            	viewHolder = (ViewHolder) convertView.getTag();
            }
            
            viewHolder.position = position ;
            Const.ContactGroup c = Const.groups.get(position);
            
            if (c != null) 
            {
            	// check box
            	viewHolder.checkbox.setChecked(c.bSelectAllUsers);
            	
                //Name
            	viewHolder.toptext = (TextView) convertView.findViewById(R.id.text1);
            	viewHolder.toptext.setText(c.GroupName);
                //Const.MyLog("ContactsList groupname=", c.GroupName, false);
                //count
                viewHolder.bottomtext = (TextView) convertView.findViewById(R.id.text2);
                String sCnt ;
                
                if (!Const.searchWord.equals(""))
                   sCnt = "" + c.contactsFound.size() ;
                else if (c.contactsCount < 0)
                   sCnt = "-";
                else 
                   sCnt = ""+c.contactsCount	;
                
                viewHolder.bottomtext.setText(""+Const.getSelectedCount(c)+"/"+sCnt);
                //Const.MyLog("ContactsList count=", "count="+c.contactsCount, false);
                
                viewHolder.select_group = (LinearLayout) convertView.findViewById(R.id.select_group);
                if ((!Const.searchWord.equals("")) && (c.contactsFound.size() > 0))
                	viewHolder.select_group.setBackgroundColor(0xfffe961b); // orange
                else
                    viewHolder.select_group.setBackgroundColor(Color.TRANSPARENT); //
            }
            
            return convertView;
        }
        private Context mContext;

    }
    public static class ViewHolder {
    	int position ;
		LinearLayout select_group ;
    	CheckBox checkbox;
        TextView toptext;
        TextView bottomtext;
    }
    
	public void onBackPressed()
	{
		setResult(RESULT_OK);
		finish() ;
	}
	

    @Override
    public void onDestroy() 
    {
    	Const.contactsList = null ;
        super.onDestroy();
    }
    
}