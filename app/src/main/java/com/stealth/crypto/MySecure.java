package com.stealth.crypto;

import java.security.SecureRandom;

public class MySecure 
{
    
    public String makech(int ch, int odd)
    {
        int val ;
        if (odd==0)
        {
           if (ch <= 9)   
              val = ch + '0' ; 
           else
              val = ch-10 + 'A' ;
        }
        else
        {
            val = ch + 'A' ;
        }
        return String.format("%c", val) ;
    }
    
    public int makenum(int ch, int odd)
    {
   	    if (odd == 0) // 0..9,A,B
 	    {
		      if (ch >= 'A')
			         return ch-'A'+10 ;
		      else
			         return ch-'0';
        }
    	else
    	    return ch-'A';
    }
    
    public String encrypt(String str)
    {
    	String rstr = "" ;
    	int MinChars = 20 ;
    	int len = str.length() ;
    	
    	for (int j=len; j < MinChars; j++) // attach '@'
    		str += '@' ;

    	int n = str.length() ;
    	for (int i=0; i < n; i++)
    	{
    		int n1,n2;
    		int nleft, nright, nsum ;
    		
    		if (i==0)
    			n1 = n-1 ;
    		else
    			n1 = i-1 ;
    		
    		n2 = i ;
    	
    		nleft = (str.charAt(n1) % 8) * 32 ;
    		nright = (str.charAt(n2) / 8);
    		
    		nsum = nleft + nright ;
    		nleft = nsum / 16 ;
    		nright = nsum % 16 ;
    		rstr = rstr + makech(nleft, i % 2) + makech(nright, i % 2) ;
    	}
    	return rstr ;
    }
    
    public String decrypt(String str)
    {
    	if (str.equals(""))
    		return "" ;
    	
    	String rstr = "" ;
    	
    	int nums[] = new int[10000];

    	int n = str.length() / 2;
    	
    	for (int i=0; i < n; i++)
    	{
    	   nums[i] = makenum(str.charAt(i*2), i % 2) * 16 + makenum(str.charAt(i*2+1), i % 2);
    	}

    	for (int i=0; i < n; i++)
    	{  
    	   int num1 = nums[i] ;
    	   int num2 ;
    	   if ( i == n-1)
    	      num2 = nums[0]; 
    	   else
    	      num2 = nums[i+1];
    	   

    	   rstr = rstr + String.format("%c", (num1 % 32 * 8) + (num2 /32));
    	}
    	
    	// exclude '@'
    	rstr = rstr.replace("@", " ");
    	rstr = rstr.trim();
    	return rstr ;
    }
    
    
	public int getRandom(int max) 
	{
		// get 0..max random number
        SecureRandom rnd = new SecureRandom();

       return(rnd.nextInt(max));

	}

    
}
