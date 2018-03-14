package com.stealth.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.stealth.service.Const;

public class SocketClient
{

	private Socket socket = null;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;

	private int port;
	private String host ;
	
	public SocketClient(String host, int port)
	{
		this.host = host ;
		this.port = port;
	}
	
	String changeToString(char[] buf, int len)
	{
		String str = "" ;
		for (int i=0; i < len ; i++)
		{
			char ch = buf[i];
			str += Character.toString(ch);
		}
		return str ;
	}
	
	public String readServerMessage()
	{
		try 
		{		
			socket.setSoTimeout(5*60*1000);
			InputStream is = socket.getInputStream() ;
			//char buf[] = new char[5000];
			byte buf[] = new byte[50000];
			int n =0 ;
			String str="";

			//InputStreamReader isr = new InputStreamReader(is);
			BufferedInputStream in =  new BufferedInputStream(is);

			n = in.read(buf, 0, buf.length);
			//while((n = in.read(buf, 0, buf.length)) != -1)
				str += new String(buf, 0, n);

				/*
				while ((n = isr.read(buf)) != -1)
				//out.write(byteBuffer, 0, recvMsgSize);
				str += changeToString(buf, n);
				*/

			return str ;
		}
		catch (IOException e) 
		{ 
			e.printStackTrace(); 
		    return "";
		}
	}
	
	   public boolean sendBytesToServer(byte[] bmsg)
	   {
	      try{
	         this.bos = new BufferedOutputStream( this.socket.getOutputStream());
	         this.bos.write(bmsg); // send msg to server
	         this.bos.flush();
	         //this.bos.close();
	         return true ;
	      }catch(Exception e){
	         this.closeSocket();
	         return false ;
	      }
	   }
	   
    public boolean sendStrToServer(String msg)
    {
    	try
    	{
    	    byte[] bmsg = msg.getBytes("UTF-8") ;
    	    return (sendBytesToServer(bmsg));
    	}
    	catch(Exception e)
    	{
    		return false ;
    	}
    }

	public boolean openSocket()
	{
		try{
			this.socket = new Socket(this.host, this.port);
			boolean result = this.socket.isConnected();
			if (result)
			   this.socket.setSoTimeout(Const.socket_time_out);
			return result ;
		}
		catch (Exception e)
		{
			System.out.println("Socket exception"+e.toString());
			return false ;
		}
	}
	
	public void closeSocket(){
		if( this.bis != null ){
			try{
				this.bis.close();
			}catch(Exception e){}
			this.bis = null;
		}
		
		if( this.socket != null ){
			try{
				this.socket.close();
			}catch(Exception e){}
			this.socket = null;
		}
	}
	
}
