package com.stealth.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class FileFunc
{
	final int MAX_BUF = 8000 ;
	BufferedInputStream reader = null;
	int filePos = 0 ;
	int prevPos = 0 ; // line start pos
	public String sLine ;
	BufferedWriter out = null ;
	
	public ArrayList<String> getDirList(String sDir) 
	{
		ArrayList<String> mDirContent;
		mDirContent = new ArrayList<String>();
		File file = new File(sDir);
		
		if (file.exists() && file.canRead()) 
		{
			String[] list = file.list();
			int len = list.length;
			
			/* add files/folder to arraylist depending on hidden status */
			for (int i = 0; i < len; i++) 
			{
					mDirContent.add(list[i]);
			}
		}
		return mDirContent;
	}	
	
	public boolean isWritableDirectory(String sDir)
	{
		File dir = new File(sDir);
		
	    return (dir.isDirectory() && dir.canRead() && dir.canWrite());
	}
	
	public boolean isDirectory(String sFile) 
	{
		File dir = new File(sFile);
		return dir.isDirectory() && dir.canRead();
	}	
	
	public boolean deleteFile(String sFile)
	{
		File f = new File(sFile);
	    return f.delete() ;
	}
	
	public boolean makeFolder(String path)
	{
	    File file = new File(path);

		if (!file.exists() && !file.mkdirs()) 
		{
		    // Can not make the directory
			return false ;
		} 
		else
		   return true;
	}

	public void close()
	{
		try
		{
			if (reader != null)
		       reader.close();
			if (out != null)
				out.close();
		}
		catch (Exception e) {}
		reader = null ;
		out=null ;
	}
	
	public boolean openRead(String sFile)
	{
		try
		{
		    reader = new BufferedInputStream(new FileInputStream(sFile));
		}
		catch (Exception e)
		{
			reader = null ;
			return false ;
		}
		return true ;
	}
	
	public boolean openWrite(String sFile)
	{
		try
		{
			out = new BufferedWriter(new FileWriter(sFile));
			
		}
		catch (Exception e)
		{
				out = null ;
				return false ;
		}
		return true ;
	}
	
	boolean rename(String sFileName, String newName)
	{
		if (reader != null)
		{
			try{
			reader.close();
			}
			catch (Exception e)
			{
				return false ;
			}
		}
		
		File f = new File(sFileName);
		File f1 = new File(newName);
		f1.delete();
		
	    f.renameTo(f1);
	    return true ;
	}
	

    public String readLine() 
    {
        byte[] buffer = new byte[MAX_BUF];
        int i=0;
        int ch ;
        int len = 0;
        
        while (i < MAX_BUF-1)
        {
        	try {
                ch = reader.read(); // Ignore the result
            	}
            catch (Exception e) 
            	{
            	if (i==0)
            		return null ;
            	ch = -1 ;	
            	}
        	
        	if (ch < 0)
        		break ;
        	else if (ch == 0x0a)
        	{
        		i++ ;
        		break ;
        	}
        	else 
        	{
        		if (ch != 0x0d)
        	    {
        		    buffer[len] = (byte)ch ;
        		    len++;
        	    }
        		i++;
        	}
        }
        
        if (i==0)
        	return null ;
        
        prevPos = filePos ;
        filePos += i ;
        
        byte[] buf = new byte[len] ;
        for (int j=0; j < len; j++)
        	buf[j] = buffer[j];
        
        // Hangul code conversion ms949 = 2byte hangul
        try{
        return new String(buf, "ms949");
        }
        catch (Exception e)
        {
        	return "" ;
        }
        
    }
    
    public boolean writeLine(String str)
    {
    	try
    	{
	        out.write(str); 
	        out.newLine();
    	}
    	catch(Exception e)
    	{
    		return false ;
    	}
    	return true ;
    }

}

