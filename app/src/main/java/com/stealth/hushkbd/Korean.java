/*
* Released under the MIT license
* Copyright (c) 2014 KimYs(a.k.a ZeDA)
* http://blog.naver.com/irineu2
* 
Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/

package com.stealth.hushkbd;

import com.stealth.service.Const;
import com.stealth.hushkbd.*;
import com.stealth.hushkbd.ComKbd;
import com.stealth.hushkbd.Const1;

import android.view.inputmethod.InputConnection;

public class Korean
{
	// code for chunjiin
    private static final int korean_btn_ngm = 210 ;
	private static final int korean_btn_i = 201;	
	private static final int korean_btn_dot = 202;	
	private static final int korean_btn_eu = 203;	
	private static final int korean_btn_gk = 204;	
	private static final int korean_btn_nr = 205;	
	private static final int korean_btn_dt = 206;	
	private static final int korean_btn_bp = 207;	
	private static final int korean_btn_sh = 208;	
	private static final int korean_btn_jch = 209;
	
	
	//code for qwerty
	private static final int korean_btn_b = 301;
	private static final int korean_btn_j = 302;
	private static final int korean_btn_d = 303;
	private static final int korean_btn_g = 304;
	private static final int korean_btn_s = 305;
	private static final int korean_btn_yo = 306;
	private static final int korean_btn_yeo = 307;
	private static final int korean_btn_ya = 308;
	private static final int korean_btn_ae = 309;
	private static final int korean_btn_e = 310;

	private static final int korean_btn_m = 311;
	private static final int korean_btn_n = 312;
	private static final int korean_btn_ng = 313;
	private static final int korean_btn_r = 314;
	private static final int korean_btn_h = 315;
	private static final int korean_btn_o = 316;
	private static final int korean_btn_eo = 317;
	private static final int korean_btn_a = 318;
	private static final int korean_btn_k = 320;
	private static final int korean_btn_t = 321;
	private static final int korean_btn_ch = 322;
	private static final int korean_btn_p = 323;
	
	private static final int korean_btn_yu = 324;
	private static final int korean_btn_wu = 325;
	private static final int korean_btn_bb = 331;
	private static final int korean_btn_jj = 332;
	private static final int korean_btn_dd = 333;
	private static final int korean_btn_gg = 334;
	private static final int korean_btn_ss = 335;
	private static final int korean_btn_yae = 336;
	private static final int korean_btn_ye = 337;

	private static final int emoticon_401 = 401;
	private static final int emoticon_402 = 402;
	private static final int emoticon_403 = 403;
	private static final int emoticon_404 = 404;
	private static final int emoticon_405 = 405;
	
	
	private static final int korean_btn_space = -32;
	private static final int korean_btn_return = 10;	
	private static final int korean_btn_backsp = -5;	
	
	private static final int korean_btn_exclaques = -11; // !? key 	
	private static final int korean_btn_dotcomma = -12; // ., key 	
	
    private static final String jung_dot = "•" ;
    private static final String jung_dot_dot = "••" ;
//	private Button btn[];
//	private EditText et;
    
	InputConnection inputConnection = null ;
	
	private static class hangul
	{
		public static String chosung = "";
		public static String jungsung = "";
		public static String jongsung = "" ; // jongsung1 + jongsung2 
		
		public static String jongsung1 = ""; // if jongsung != jongsung1, jongsung = jongsung1+jongsung2
		                              // else jongsung2 is a candidate of next syllable
		public static String jongsung2 = "";
		
		// check values for repeated key
		private static int last_input = 0 ;
		private static int last_input_cnt = 0 ;
		
		
	    public static long last_input_time =  0;
		
		public static void init()
		{
			chosung = "";
			jungsung = "";
			jongsung = "" ;
			
			jongsung1 = "";
			jongsung2 = "";
			
	        last_input_time =  System.currentTimeMillis(); 
	        last_input_cnt = 0 ;
	        if (Const1.getHangulKbdType() == Const1.HAN_CHUNJIIN)
	        {
	        	last_input = 0 ;
	        }

		}

		public static boolean isEmpty()
		{
			if (chosung.equals("") &&
					jungsung.equals("") &&
							jongsung.equals(""))
				return true ;
			else
				return false ;
		}

		public static String setHangul(String cho, String jung, String jong1, String jong2)
		{
			// return overflowed char
			chosung = cho ;
			jungsung = jung ;
			jongsung1 =jong1 ;
			jongsung2 = jong2 ;
			String jong = makeDouble(jong1, jong2);
			if (jong.equals(""))
			{
				jongsung2 = "" ;
				jongsung = jong1 ;
				return jong2 ;
			}
			else
			{
				jongsung = jong ;
			}
			return "" ;
		}
	}

	// 복합 받침 처리
	// C0+V+C3(C1+C2) -> 받침은 복합 가능하면 C3 = C1+C2 아니면 C1
	// display        -> 복합 불가능시 2글자로  C0+V+C1, C2로 display
	// 예: 핥 입력시 'ㅎ' - 'ㅏ' - 'ㄹ' - 'ㄷ' - 'ㅌ'(ㄷ 또 누름)
	//            ㅎ          하          할    - 할ㄷ   -  핥

	public static void init()
	{
		hangul.init();
		hangul.last_input = 0 ;
		hangul.last_input_cnt = 0 ;
	}

    public static void checkHangulFinish(InputConnection ic, int input)
    {
    	if (Const1.getHangulKbdType() == Const1.HAN_CHUNJIIN)
    	{
            long diff = System.currentTimeMillis() - hangul.last_input_time ;

            if (diff > Const.FINISH_HANGUL_TIME)
            {
				if (hangul.last_input == input)
				{
					hangul.last_input = 0 ;
					finishHangul(ic);
				}
            }
    	}
    }


	private static void addString(String str)
	{
		// add composed hangul
		com.stealth.hushkbd.ComKbd.mComposing.append(str);
	}

	public static boolean existTempHangul()
	{
		if ((hangul.chosung.equals("")) &&
		   (hangul.jungsung.equals("")) &&
		   (hangul.jongsung.equals("")))
		    return false ;
		else
			return true ;
	}

	public static boolean existTempSyllableHangul()
	{
		//글자가 되는 한글 있을 때
		if ((!hangul.chosung.equals("")) &&
		   (!hangul.jungsung.equals("")))
		    return true ;
		else
			return false ;
	}

	public static void dspComposing(InputConnection inputConnection)
	{
		if (existTempHangul())
		{
			String temp = "" ;
			if ((hangul.jungsung == jung_dot) || (hangul.jungsung == jung_dot_dot))
			{
				temp = hangul.chosung + hangul.jungsung ;
			}
			else
			{
				temp = getUnicodeStr(hangul.chosung, hangul.jungsung, hangul.jongsung) ;
			    if (temp.equals("")) // can't composing
				{
					com.stealth.hushkbd.ComKbd.mComposing.append(getUnicodeStr(hangul.chosung, hangul.jungsung, ""));
					hangul.chosung = hangul.jongsung;
					hangul.jungsung = "";
					hangul.jongsung = "";
					temp = hangul.jongsung;
				}
			}

			if ((hangul.jongsung2.length() > 0) && hangul.jongsung.equals(hangul.jongsung1)) // not composable
				temp += hangul.jongsung2 ;
           inputConnection.setComposingText(com.stealth.hushkbd.ComKbd.mComposing.toString()+temp, 1);
		}
		else
		{
			if (com.stealth.hushkbd.ComKbd.mComposing.length() > 0)
               inputConnection.setComposingText(com.stealth.hushkbd.ComKbd.mComposing.toString(), 1); //ysk20150912
               //inputConnection.setComposingText(ComKbd.mComposing.toString(), ComKbd.mComposing.toString().length()); //ysk20150912
			else
               inputConnection.setComposingText("", 0);
		}
	}

	private static int getUnicode(String chosung, String jungsung, String jongsung)
	{
		int cho, jung, jong;
		//초성
		if(chosung.length() == 0)
		{
			if(jungsung.length() == 0 || jungsung.equals(jung_dot) || jungsung.equals(jung_dot_dot))
				return 0;
		}

		if ( chosung.equals("ㄱ"))	cho = 0;
		else if ( chosung.equals("ㄲ"))	cho = 1;
		else if ( chosung.equals("ㄴ"))	cho = 2;
		else if ( chosung.equals("ㄷ"))	cho = 3;
		else if ( chosung.equals("ㄸ"))	cho = 4;
		else if ( chosung.equals("ㄹ"))	cho = 5;
		else if ( chosung.equals("ㅁ"))	cho = 6;
		else if ( chosung.equals("ㅂ"))	cho = 7;
		else if ( chosung.equals("ㅃ"))	cho = 8;
		else if ( chosung.equals("ㅅ"))	cho = 9;
		else if ( chosung.equals("ㅆ"))	cho = 10;
		else if ( chosung.equals("ㅇ"))	cho = 11;
		else if ( chosung.equals("ㅈ"))	cho = 12;
		else if ( chosung.equals("ㅉ"))	cho = 13;
		else if ( chosung.equals("ㅊ"))	cho = 14;
		else if ( chosung.equals("ㅋ"))	cho = 15;
		else if ( chosung.equals("ㅌ"))	cho = 16;
		else if ( chosung.equals("ㅍ"))	cho = 17;
		else /*if ( chosung.equals("ㅎ"))*/	cho = 18;

		if (jungsung.length() == 0 && jongsung.length() == 0)
			return 0x1100 + cho;
		if (jungsung.equals(jung_dot) || jungsung.equals(jung_dot_dot))
			return 0x1100 + cho;

		// 중성
		if ( jungsung.equals("ㅏ"))		jung = 0;
		else if ( jungsung.equals("ㅐ"))	jung = 1;
		else if ( jungsung.equals("ㅑ"))	jung = 2;
		else if ( jungsung.equals("ㅒ"))	jung = 3;
		else if ( jungsung.equals("ㅓ"))	jung = 4;
		else if ( jungsung.equals("ㅔ"))	jung = 5;
		else if ( jungsung.equals("ㅕ"))	jung = 6;
		else if ( jungsung.equals("ㅖ"))	jung = 7;
		else if ( jungsung.equals("ㅗ"))	jung = 8;
		else if ( jungsung.equals("ㅘ"))	jung = 9;
		else if ( jungsung.equals("ㅙ"))	jung = 10;
		else if ( jungsung.equals("ㅚ"))	jung = 11;
		else if ( jungsung.equals("ㅛ"))	jung = 12;
		else if ( jungsung.equals("ㅜ"))	jung = 13;
		else if ( jungsung.equals("ㅝ"))	jung = 14;
		else if ( jungsung.equals("ㅞ"))	jung = 15;
		else if ( jungsung.equals("ㅟ"))	jung = 16;
		else if ( jungsung.equals("ㅠ"))	jung = 17;
		else if ( jungsung.equals("ㅡ"))	jung = 18;
		else if ( jungsung.equals("ㅢ"))	jung = 19;
		else /*if ( jungsung.equals("ㅣ"))*/	jung = 20;

		if ( chosung.length() == 0 && jongsung.length() == 0)
			return 0x1161 + jung;

		// 종성
		if ( jongsung.length() == 0)		jong = 0;
		else if ( jongsung.equals("ㄱ"))	jong = 1;
		else if ( jongsung.equals("ㄲ"))	jong = 2;
		else if ( jongsung.equals("ㄳ"))	jong = 3;
		else if ( jongsung.equals("ㄴ"))	jong = 4;
		else if ( jongsung.equals("ㄵ"))	jong = 5;
		else if ( jongsung.equals("ㄶ"))	jong = 6;
		else if ( jongsung.equals("ㄷ"))	jong = 7;
		else if ( jongsung.equals("ㄹ"))	jong = 8;
		else if ( jongsung.equals("ㄺ"))	jong = 9;
		else if ( jongsung.equals("ㄻ"))	jong = 10;
		else if ( jongsung.equals("ㄼ"))	jong = 11;
		else if ( jongsung.equals("ㄽ"))	jong = 12;
		else if ( jongsung.equals("ㄾ"))	jong = 13;
		else if ( jongsung.equals("ㄿ"))	jong = 14;
		else if ( jongsung.equals("ㅀ"))	jong = 15;
		else if ( jongsung.equals("ㅁ"))	jong = 16;
		else if ( jongsung.equals("ㅂ"))	jong = 17;
		else if ( jongsung.equals("ㅄ"))	jong = 18;
		else if ( jongsung.equals("ㅅ"))	jong = 19;
		else if ( jongsung.equals("ㅆ"))	jong = 20;
		else if ( jongsung.equals("ㅇ"))	jong = 21;
		else if ( jongsung.equals("ㅈ"))	jong = 22;
		else if ( jongsung.equals("ㅊ"))	jong = 23;
		else if ( jongsung.equals("ㅋ"))	jong = 24;
		else if ( jongsung.equals("ㅌ"))	jong = 25;
		else if ( jongsung.equals("ㅍ"))	jong = 26;
		else if ( jongsung.equals("ㅎ"))   jong = 27;
		else
		    return -1 ;

		if ( chosung.length() == 0 && jungsung.length() == 0)
			return 0x11a8 + jong;

		return 44032 + cho*588 + jung*28 + jong;
	}


	private static String getUnicodeStr(String cho, String jung, String jong)
	{
		if ((cho.length() == 0) && (jong.length() == 0) &&
				((jung.equals(jung_dot) || (jung.equals(jung_dot_dot)))))
			return jung ;


		int unicode = getUnicode(cho, jung, jong) ;
		if (unicode <= 0) return "" ;
		else
		   return String.valueOf((char)unicode) ;
	}


	private static void delete()
	{
		if (!hangul.jongsung2.equals(""))
		{
			hangul.jongsung2 = "" ;
		    hangul.jongsung = hangul.jongsung1 ;
	    }
		else if (!hangul.jongsung.equals(""))
		{
			hangul.jongsung = "" ;
			hangul.jongsung1 = "" ;
		}
		else if (!hangul.jungsung.equals(""))
		{
			hangul.jungsung = "" ;
		}
		else if (!hangul.chosung.equals(""))
		{
			hangul.chosung = "" ;
		}
		else // delete prev char
		{
			com.stealth.hushkbd.ComKbd.delete() ;
		}

	}

	public static void finishHangul(InputConnection inputConnection)
	{
		if (existTempHangul())
		{
			addString(getUnicodeStr(hangul.chosung, hangul.jungsung, hangul.jongsung)) ;
			hangul.init() ;
			dspComposing(inputConnection) ;
		}
	}

	private static String makeDouble(String jong1, String jong2)
	{
		String s = "";
		if (jong1.equals("ㄱ"))
		{
			if (jong2.equals("ㅅ"))		s = "ㄳ";
		}
		else if (jong1.equals("ㄴ"))
		{
			if (jong2.equals("ㅈ"))		s = "ㄵ";
			else if (jong2.equals("ㅎ"))	s = "ㄶ";
		}
		else if (jong1.equals("ㄹ"))
		{
			if (jong2.equals("ㄱ"))		s = "ㄺ";
			else if (jong2.equals("ㅁ"))	s = "ㄻ";
			else if (jong2.equals("ㅂ"))	s = "ㄼ";
			else if (jong2.equals("ㅅ"))	s = "ㄽ";
			else if (jong2.equals("ㅌ"))	s = "ㄾ";
			else if (jong2.equals("ㅍ"))	s = "ㄿ";
			else if (jong2.equals("ㅎ"))	s = "ㅀ";
		}
		else if (jong1.equals("ㅂ"))
		{
			if (jong2.equals("ㅅ"))		s = "ㅄ";
		}
		return s;
	}


    private static int checkGoogleDouble(InputConnection ic, int input)
	{
		// ㄱ + ㄱ = delete and ㄲ
    	int ret = input ;

		switch (input)
		{
		case 301 : delete() ;  ret = 331 ; break ;
		case 302 : delete() ;  ret = 332 ; break ;
		case 303 : delete() ;  ret = 333 ; break ;
		case 304 : delete() ;  ret = 334 ; break ;
		case 305 : delete() ;  ret = 335 ; break ;
		case 316 : delete() ;  ret = 306 ; break ;
		case 309 : delete() ;  ret = 336 ; break ;
		case 310 : delete() ;  ret = 337 ; break ;

		case 317 : delete() ;  ret = 307 ; break ;
		case 318 : delete() ;  ret = 308 ; break ;
		case korean_btn_dotcomma : delete() ; addString(","); ret = 0 ; break ;
		}

		return ret ;
	}

	public static void hangulMake( InputConnection ic, int input)
	{

		boolean bReplace = false ;

		if (input > 0 && input < 200)
		{
			finishHangul(ic) ;
            ComKbd.mComposing.append((char) input);
            dspComposing(ic);
    		hangul.last_input_time = System.currentTimeMillis() ;
            return ;
		}

        if (input > 400)
		{
			finishHangul(ic) ;
			hangul.last_input = 0 ;
			hangul.last_input_cnt = 0 ;
			if (input == emoticon_401)
				addString("...") ;
			else if (input == emoticon_402)
				addString("ㅋㅋ") ;
			else if (input == emoticon_403)
				addString("ㅎㅎ") ;
			else if (input == emoticon_404)
				addString("^^") ;
			else
				addString("ㅠㅠ") ;
			dspComposing(ic) ;
			hangul.last_input_time = System.currentTimeMillis() ;
			return ;
		}

        if (Const1.getHangulKbdType() == Const1.HAN_CHUNJIIN)
		{
			if (hangul.last_input == input)
				hangul.last_input_cnt++ ;
			else
			{
				hangul.last_input_cnt = 0 ;
			}
			hangul.last_input = input ;
		}
		else if (Const1.getHangulKbdType() == Const1.HAN_GOOGLE)
		{
			Const.MyLog("onKey", "1.last_input="+ hangul.last_input);
			if (hangul.last_input == input)
			{
				hangul.last_input_cnt++ ;
				Const.MyLog("onKey", "last_input_cnt="+ hangul.last_input_cnt);
			}
			else
				hangul.last_input_cnt = 0 ;

			long diff_time = System.currentTimeMillis() - hangul.last_input_time ;

			if (diff_time <= Const.DOUBLE_CLICK_TIME && hangul.last_input_cnt > 0)
			{
				Const.MyLog("onKey", "before checkGoogle");
				input = checkGoogleDouble(ic, input) ;
				Const.MyLog("onKey", "after checkGoogle="+input);
				hangul.last_input = 0 ;
			}
			else
				hangul.last_input = input ;

			Const.MyLog("onKey", "last_input====="+ hangul.last_input);
			hangul.last_input_cnt = 0 ;
		}
		else // qwerty
		{
			hangul.last_input = 0 ;
			hangul.last_input_cnt = 0 ;
		}

		hangul.last_input_time = System.currentTimeMillis() ;

		if (input == korean_btn_exclaques)
		{
			String str = "!";

			if (hangul.last_input_cnt == 0)
			{
				finishHangul(ic) ;
			}
			else
			{
				delete();
				if (hangul.last_input_cnt % 2 == 1)
					str = "?" ;
			}
			addString(str);
		}
		else if (input == korean_btn_dotcomma)
		{
			String str = ".";

			if (hangul.last_input_cnt == 0)
			{
				finishHangul(ic) ;
			}
			else
			{
				delete();
				if (hangul.last_input_cnt % 2 == 1)
					str = "," ;
			}
			addString(str);
		}
		else if (input == korean_btn_space) //띄어쓰기
		{
			if (existTempHangul())
			{
				if (Const1.getHangulKbdType() == Const1.HAN_CHUNJIIN)
				   finishHangul(ic) ;
				else
				{
					finishHangul(ic) ;
					addString(" ");
				}
			}
			else
			    addString(" ") ;
		}
		else if (input == korean_btn_return)
		{
			finishHangul(ic) ;
			addString("\n") ;
		}
		else if(input == korean_btn_backsp) //지우기
		{
			delete() ;
		}
		else
		{
			hangulMake1(ic, input);
		}
		dspComposing(ic) ;
	}


	private static void hangulMake1( InputConnection ic, int input)
	{
		String moum = "" ;
		String jaum = "" ;
		int n ;

		switch (input)
		{
		    case korean_btn_i  :
		    	moum = "ㅣ" ; break ;
		    case korean_btn_dot :
	    		moum = jung_dot ; break ;
		    case korean_btn_eu  :
		    	moum = "ㅡ" ; break ;
			case korean_btn_yo :
		    	moum = "ㅛ" ; break ;
			case korean_btn_yeo :
		    	moum = "ㅕ" ; break ;
			case korean_btn_ya :
		    	moum = "ㅑ" ; break ;
			case korean_btn_ae :
		    	moum = "ㅐ" ; break ;
			case korean_btn_e :
		    	moum = "ㅔ" ; break ;
			case korean_btn_o :
		    	moum = "ㅗ" ; break ;
			case korean_btn_eo:
		    	moum = "ㅓ" ; break ;
			case korean_btn_a :
		    	moum = "ㅏ" ; break ;
			case korean_btn_yu :
		    	moum = "ㅠ" ; break ;
			case korean_btn_wu :
		    	moum = "ㅜ" ; break ;
			case korean_btn_yae :
		    	moum = "ㅒ" ; break ;
			case korean_btn_ye :
		    	moum = "ㅖ" ; break ;

			case korean_btn_ngm :
	    		n = hangul.last_input_cnt % 2 ;
	    		if (n == 0) jaum = "ㅇ" ;
	    		else jaum = "ㅁ" ;
	    		break ;

	    	case korean_btn_gk :
	    		n = hangul.last_input_cnt % 3 ;
	    		if (n == 0) jaum = "ㄱ" ;
	    		else if (n == 1) jaum = "ㅋ" ;
	    		else jaum = "ㄲ";
	    		break ;

	    	case korean_btn_nr :
	    		n = hangul.last_input_cnt % 2 ;
	    		if (n == 0) jaum = "ㄴ" ;
	    		else jaum = "ㄹ";
	    		break ;

	    	case korean_btn_dt :
	    		n = hangul.last_input_cnt % 3 ;
	    		if (n == 0) jaum = "ㄷ" ;
	    		else if (n == 1) jaum = "ㅌ" ;
	    		else
					jaum = "ㄸ";
	    		break ;

	    	case korean_btn_bp :
	    		n = hangul.last_input_cnt % 3 ;
	    		if (n == 0) jaum = "ㅂ" ;
	    		else if (n == 1) jaum = "ㅍ" ;
	    		else jaum = "ㅃ";
	    		break ;

	    	case korean_btn_sh :
	    		n = hangul.last_input_cnt % 3 ;
	    		if (n == 0)
					jaum = "ㅅ" ;
	    		else if (n == 1)
					jaum = "ㅎ" ;
	    		else
					jaum = "ㅆ";
	    		break ;

	    	case korean_btn_jch :
	    		n = hangul.last_input_cnt % 3 ;
	    		if (n == 0)
					jaum = "ㅈ" ;
	    		else if (n == 1)
					jaum = "ㅊ" ;
	    		else
					jaum = "ㅉ";
	    		break ;

			case korean_btn_b : // 301;
				jaum = "ㅂ" ; break ;
			case korean_btn_j : // 302;
				jaum = "ㅈ" ; break ;
			case korean_btn_d : // 303;
				jaum = "ㄷ" ; break ;
			case korean_btn_g : // 304;
				jaum = "ㄱ" ; break ;
			case korean_btn_s : // 305;
				jaum = "ㅅ" ; break ;
			case korean_btn_m : // 311;
				jaum = "ㅁ" ; break ;
			case korean_btn_n : // 312;
				jaum = "ㄴ" ; break ;
			case korean_btn_ng : // 313;
				jaum = "ㅇ" ; break ;
			case korean_btn_r : // 314;
				jaum = "ㄹ" ; break ;
			case korean_btn_h : // 315;
				jaum = "ㅎ" ; break ;
			case korean_btn_k : // 320;
				jaum = "ㅋ" ; break ;
			case korean_btn_t : // 321;
				jaum = "ㅌ" ; break ;
			case korean_btn_ch : // 322;
				jaum = "ㅊ" ; break ;
			case korean_btn_p : // 323;
				jaum = "ㅍ" ; break ;
			case korean_btn_bb : // 331;
				jaum = "ㅃ" ; break ;
			case korean_btn_jj : // 332;
				jaum = "ㅉ" ; break ;
			case korean_btn_dd : // 333;
				jaum = "ㄸ" ; break ;
			case korean_btn_gg : // 334;
				jaum = "ㄲ" ; break ;
			case korean_btn_ss : // 335;
				jaum = "ㅆ" ; break ;

		}

		if (!jaum.equals(""))
			jaumMake(ic, jaum);
		else if (!moum.equals(""))
			moumMake(ic, moum);
	}

	private static void moumMake(InputConnection ic, String moum)
	{
		String beforejung = "";
		String nowjung = "";

			//모음
			//받침에서 떼어오는거 추가해야함
			boolean batchim = false;
			String chosung = "";
			if (hangul.jongsung.length() > 0)
			{
				// 받침이 복합
				if (hangul.jongsung2.length() > 0)
				{
					addString(getUnicodeStr(hangul.chosung, hangul.jungsung, hangul.jongsung1)) ;
					chosung = hangul.jongsung2 ;
				}
				else
				{
					addString(getUnicodeStr(hangul.chosung, hangul.jungsung, "")) ;
					chosung = hangul.jongsung ;
				}
				hangul.init();
				hangul.chosung = chosung ;
			}

			beforejung = hangul.jungsung;

			if (moum.equals("ㅣ")) // ㅣ ㅓ ㅕ ㅐ ㅔ ㅖㅒ ㅚ ㅟ ㅙ ㅝ ㅞ ㅢ
			{
				if (beforejung.length() == 0)
					nowjung = "ㅣ";
				else if (beforejung.equals(jung_dot))
				{
					nowjung = "ㅓ";
				}
				else if (beforejung.equals(jung_dot_dot))
				{
					nowjung = "ㅕ";
				}
				else if(beforejung.equals("ㅏ"))	nowjung = "ㅐ";
				else if(beforejung.equals("ㅑ"))	nowjung = "ㅒ";
				else if(beforejung.equals("ㅓ"))	nowjung = "ㅔ";
				else if(beforejung.equals("ㅕ"))	nowjung = "ㅖ";
				else if(beforejung.equals("ㅗ"))	nowjung = "ㅚ";
				else if(beforejung.equals("ㅜ"))	nowjung = "ㅟ";
				else if(beforejung.equals("ㅠ"))	nowjung = "ㅝ";
				else if(beforejung.equals("ㅘ"))	nowjung = "ㅙ";
				else if(beforejung.equals("ㅝ"))	nowjung = "ㅞ";
				else if(beforejung.equals("ㅡ"))	nowjung = "ㅢ";
				else
				{
					finishHangul(ic);
					nowjung = "ㅣ";
				}
			}
			else if (moum.equals(jung_dot)) // ·,‥,ㅏ,ㅑ,ㅜ,ㅠ,ㅘ
			{
				if (beforejung.length() == 0)
				{
					nowjung = jung_dot;
				}
				else if (beforejung.equals(jung_dot))
				{
					nowjung = jung_dot_dot;
				}
				else if (beforejung.equals(jung_dot_dot))
				{
					nowjung = jung_dot;
				}
				else if(beforejung.equals("ㅣ"))	nowjung = "ㅏ";
				else if(beforejung.equals("ㅏ"))	nowjung = "ㅑ";
				else if(beforejung.equals("ㅡ"))	nowjung = "ㅜ";
				else if(beforejung.equals("ㅜ"))	nowjung = "ㅠ";
				else if(beforejung.equals("ㅚ"))	nowjung = "ㅘ";
				else
				{
					finishHangul(ic);
					nowjung = jung_dot;
				}
			}
			else if (moum.equals("ㅡ")) // ㅡ, ㅗ, ㅛ
			{
				if(beforejung.length() == 0)
					nowjung = "ㅡ";
				else if(beforejung.equals(jung_dot))
				{
					nowjung = "ㅗ";
				}
				else if(beforejung.equals(jung_dot_dot))
				{
					nowjung = "ㅛ";
				}
				else
				{
					finishHangul(ic);
					nowjung = "ㅡ";
				}
			}
			else if ((moum.equals("ㅏ")) && (beforejung.equals("ㅗ")))
					nowjung = "ㅘ";
			else if ((moum.equals("ㅓ")) && (beforejung.equals("ㅜ")))
				nowjung = "ㅝ";
			else if ((moum.equals("ㅐ")) && (beforejung.equals("ㅗ")))
				nowjung = "ㅙ";
			else if ((moum.equals("ㅔ")) && (beforejung.equals("ㅜ")))
				nowjung = "ㅞ";
			else
			{
				if ((hangul.chosung.equals("")) && (!beforejung.equals("")))
				   finishHangul(ic);
				nowjung = moum ;
			}

			hangul.jungsung = nowjung;
	}



	private static void jaumMake(InputConnection ic, String jaum)
	{
		//자음
	    if (hangul.isEmpty())
	    	hangul.setHangul(jaum, "", "", "");
	    else if (hangul.jungsung.length() == 0 && hangul.jongsung.length() == 0)
	    {
	        //Cho only
	    	if (hangul.last_input_cnt > 0)
	    		hangul.setHangul(jaum, "", "", "");
	    	else
	    	{
	    		finishHangul(ic) ;
	    		hangul.setHangul(jaum, "", "", "");
	    	}
	    }
	    else if ((hangul.chosung.length() == 0) &&
	    		 (hangul.jungsung.length() > 0) &&
	    		 (hangul.jongsung.length() == 0))
	    {
	    	// jung only
			if (hangul.jungsung.equals(jung_dot) || hangul.jungsung.equals(jung_dot_dot))
				hangul.init();
			else
    		    finishHangul(ic) ;
    		hangul.setHangul(jaum, "", "", "");
	    }
	    else if ((hangul.chosung.length() > 0) &&
	    		 (hangul.jungsung.length() > 0) &&
	    		 (hangul.jongsung.length() == 0))
	    {
	    	// cho , jung
	    	if ((jaum.equals("ㄸ")) || (jaum.equals("ㅃ")) || (jaum.equals("ㅉ")))
	    	{
    		    finishHangul(ic) ;
        		hangul.setHangul(jaum, "", "", "");
	    	}
	    	else
	    	{
	    		hangul.setHangul(hangul.chosung, hangul.jungsung, jaum, "");
	    	}
	    }
	    else // cho jung jong
	    {
	    	if (hangul.jongsung2.equals(""))
	    	{
	    		if (hangul.last_input_cnt > 0)
		    		hangul.setHangul(hangul.chosung, hangul.jungsung, jaum, "");
	    		else
	    		{
	    			String over = hangul.setHangul(hangul.chosung, hangul.jungsung, hangul.jongsung1, jaum);
	    			if (Const1.getHangulKbdType() == Const1.HAN_CHUNJIIN)
	    				hangul.jongsung2 = jaum ;
	    			else
	    			{
	    				if (!over.equals(""))
	    				{
			    		    hangul.setHangul(hangul.chosung, hangul.jungsung, hangul.jongsung1, "");
	    				    finishHangul(ic);
			    		    hangul.setHangul(jaum, "", "", "");
	    				}
	    			}
	    		}
	    	}
	    	else
	    	{
	    		// cho jung jong1 jong2
	    		if (hangul.last_input_cnt > 0)
				{
					String over = hangul.setHangul(hangul.chosung, hangul.jungsung, hangul.jongsung1, jaum);
					if (Const1.getHangulKbdType() == Const1.HAN_CHUNJIIN)
						hangul.jongsung2 = jaum ;
				}
	    		else
	    		{
	    			finishHangul(ic) ;
		    		hangul.setHangul(jaum, "", "", "");
	    		}
	    	}
	    }
    }
}
