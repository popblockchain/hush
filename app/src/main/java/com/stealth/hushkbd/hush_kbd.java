/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stealth.hushkbd;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.stealth.service.ClipboardMonitor;
import com.stealth.service.Const;
import com.stealth.hushkbd.*;
import com.stealth.hushkbd.CandidateView;
import com.stealth.hushkbd.ComKbd;
import com.stealth.hushkbd.Const1;
import com.stealth.hushkbd.Korean;
import com.stealth.hushkbd.LatinKeyboard;
import com.stealth.hushkbd.LatinKeyboardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
@SuppressLint("NewApi")
public class hush_kbd extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener 
{
    // static final boolean DEBUG = false;
    
    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on 
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
	static final boolean PROCESS_HARD_KEYS = true;

    private InputMethodManager mInputMethodManager;


    private LatinKeyboardView mInputView;
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;

//    private StringBuilder ComKbd.mComposing = new StringBuilder();
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private int mLastDisplayWidth;
    private int mShiftState = 0; // 1=shift, 2=caps lock

    private long mLastShiftTime = 0;
    private long mMetaState;

    private com.stealth.hushkbd.LatinKeyboard mNumericSymbolsKeyboard = null;
    private com.stealth.hushkbd.LatinKeyboard mSymbolsShiftedKeyboard = null;
    private com.stealth.hushkbd.LatinKeyboard mQwertyKeyboard = null;
    private com.stealth.hushkbd.LatinKeyboard mChunjiinKeyboard = null;
    private com.stealth.hushkbd.LatinKeyboard mQwertyKoreanKeyboard = null;
    private com.stealth.hushkbd.LatinKeyboard mQwertyKoreanShiftKeyboard = null;
    private com.stealth.hushkbd.LatinKeyboard mGoogleKeyboard = null;

    private com.stealth.hushkbd.LatinKeyboard mCurKeyboard;

    private String mWordSeparators;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override public void onCreate() {
        super.onCreate();
        Const.hush_kbd = this ;
        mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
        Const1.prevCode = 0 ;
        dspAllGroupId() ;
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override public void onInitializeInterface()
    {
        if (mQwertyKeyboard != null)
        {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mChunjiinKeyboard = new com.stealth.hushkbd.LatinKeyboard(this, R.xml.chunjiin);
        mQwertyKoreanKeyboard = new com.stealth.hushkbd.LatinKeyboard(this, R.xml.qwerty_korean);
        mQwertyKoreanShiftKeyboard = new com.stealth.hushkbd.LatinKeyboard(this, R.xml.qwerty_korean_shift);
        mQwertyKeyboard = new com.stealth.hushkbd.LatinKeyboard(this, R.xml.qwerty);
        mNumericSymbolsKeyboard = new com.stealth.hushkbd.LatinKeyboard(this, R.xml.numeric_symbols);
        mSymbolsShiftedKeyboard = new com.stealth.hushkbd.LatinKeyboard(this, R.xml.symbols_shift);
        mGoogleKeyboard = new com.stealth.hushkbd.LatinKeyboard(this, R.xml.google);

        //Korean.init();

    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override public View onCreateInputView()
    {
        mInputView = (LatinKeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setPreviewEnabled(false); //yskang added

        dspAllGroupId();


        switch (Const1.getHangulKbdType())
        {
        case Const1.HAN_CHUNJIIN:
            setLatinKeyboard(mChunjiinKeyboard); break ;
        case Const1.HAN_QWERTY:
            setLatinKeyboard(mQwertyKoreanKeyboard); break ;
        default:
            setLatinKeyboard(mGoogleKeyboard); break ;
        }

        return mInputView;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard)
    {
        final boolean shouldSupportLanguageSwitchKey =
                mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
        nextKeyboard.setLanguageSwitchKeyVisibility(false); // shouldSupportLanguageSwitchKey);
        mInputView.setKeyboard(nextKeyboard);
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        return mCandidateView;
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting)
    {
        super.onStartInput(attribute, restarting);

        //Const1.checkDayPassed() ;

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        //ComKbd.mComposing.setLength(0);
        //updateCandidates();
        //Korean.init();

        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mNumericSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mNumericSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
            	setCurHangulKeyboard();

                mPredictionOn = true;

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false;
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false;
                }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false;
                    mCompletionOn = isFullscreenMode();
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                //updateShiftKeyState();//attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
            	setCurHangulKeyboard();
                //updateShiftKeyState();//attribute);
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override public void onFinishInput()
    {
        Korean.finishHangul(getCurrentInputConnection());

        super.onFinishInput();
        // Clear current composing text and candidates.
        ComKbd.mComposing.setLength(0);

        //Const1.checkDayPassed() ; // if day passed -> get group list / version & update groups background

        //updateCandidates();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        setCurHangulKeyboard();

        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
        mInputView.closing();
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        mInputView.setSubtypeOnSpaceKey(subtype);

        InputConnection ic = getCurrentInputConnection();
		if (Const1.curInputConnection != ic)
		{
			  Const1.curInputConnection = ic ;

	    	  // reset all
	    	  Korean.init();
	          ComKbd.mComposing.setLength(0);
		}
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */

    public void dspAllGroupId()
    {
        if (mInputView == null)
            return ;

        if (Const2.appKind != Const.APP_COMPANY && Const2.appKind != Const.APP_COMPANY_MGR)
            return ;

        mInputView.dspGroupId(mGoogleKeyboard);
        mInputView.dspGroupId(mChunjiinKeyboard);
        mInputView.dspGroupId(mQwertyKoreanKeyboard);
        mInputView.dspGroupId(mQwertyKoreanShiftKeyboard);
        mInputView.dspGroupId(mQwertyKeyboard);
        mInputView.dspGroupId(mNumericSymbolsKeyboard);
        mInputView.dspGroupId(mSymbolsShiftedKeyboard);
    }


    @Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
            int newSelStart, int newSelEnd,
            int candidatesStart, int candidatesEnd)
    {

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
    	dspAllGroupId();

        InputConnection ic = getCurrentInputConnection();

	      if ((candidatesStart == -1) && (candidatesEnd == -1)) //after enter pressed
	      {
	    	  // reset all
	    	  Korean.init();
	          ComKbd.mComposing.setLength(0);
	      }
	      else if ((ComKbd.mComposing.length() > 0 || (Korean.existTempHangul())) &&
	    		  (newSelStart != candidatesEnd || newSelEnd != candidatesEnd))
	 //       if ((newSelStart != candidatesEnd) || (newSelEnd != candidatesEnd))
	      {
	            //updateCandidates();
	            ic = getCurrentInputConnection();
	            if (ic != null)
	            {
	            	Korean.finishHangul(ic);
	                ComKbd.mComposing.setLength(0);
	                ic.finishComposingText();
	                ic.setSelection(newSelStart, newSelEnd);
	            }
	      }

	      super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
	                candidatesStart, candidatesEnd);
	    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override public void onDestroy()
    {
        Const.hush_kbd = null ;
        super.onDestroy();
    }

    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                //setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < completions.length; i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            //setSuggestions(stringList, true, true);
        }
    }

	private void setCurHangulKeyboard()
	{
    if (Const1.getHangulKbdType() == Const1.HAN_CHUNJIIN)
        mCurKeyboard = mChunjiinKeyboard;
    else if (Const1.getHangulKbdType() == Const1.HAN_QWERTY)
        mCurKeyboard = mQwertyKoreanKeyboard ;
    else
        mCurKeyboard = mGoogleKeyboard ;
	}

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (ComKbd.mComposing.length() > 0) {
            char accent = ComKbd.mComposing.charAt(ComKbd.mComposing.length() -1 );
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                ComKbd.mComposing.setLength(ComKbd.mComposing.length()-1);
            }
        }

        onKey(c, null);

        return true;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("stealthkbd.java: " , "kc="+keyCode);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (ComKbd.mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;

            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState()&KeyEvent.META_ALT_ON) != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }

        return super.onKeyUp(keyCode, event);
    }


    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */

    private void updateShiftKeyState()
    {
        Keyboard current = mInputView.getKeyboard();
        if (mQwertyKeyboard == current) {
            if (mInputView != null)
                mInputView.setShifted(mShiftState > 0);
        }
    }


    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    // getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                    ComKbd.mComposing.append(String.valueOf((char) keyCode));
                }
                break;
        }
    }

    // Implementation of KeyboardViewListener
    private boolean isKoreanKeyboard()
    {
        Keyboard current = mInputView.getKeyboard();
        if (mInputView != null &&
        	(mChunjiinKeyboard == current) ||
        	(mGoogleKeyboard == current) ||
        	(mQwertyKoreanKeyboard == current ) ||
        	(mQwertyKoreanShiftKeyboard == current))
        	return true ;
        else
        	return false ;
    }

    private void startClipboardMonitor()
    {
        if (Const.getService() == null)
        {
            ComponentName service = startService(
                    new Intent(this, ClipboardMonitor.class));
            if (service == null)
            {
                Const.MyLog("Can't start service "
                        + ClipboardMonitor.class.getName());
            }
            //startService(new Intent(this,
            //     ClipboardMonitor.class));
        }
    }

    public void onKey(int primaryCode, int[] keyCodes)
    {
        startClipboardMonitor();

    	Const.MyLog("onKey", "primaryCode="+primaryCode);
        Keyboard current = mInputView.getKeyboard();
        if (!Const.isLock())
            Const.saveLastUsedTime();

        if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_ENCRYPT) //LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH)
        {
        		InputConnection ic = getCurrentInputConnection() ;
            	Korean.finishHangul(ic);
            	String text1 = (String)ic.getTextBeforeCursor(10000, 0);
            	String text2 = (String)ic.getTextAfterCursor(10000, 0);
            	ic.deleteSurroundingText(10000, 10000);
            	ComKbd.mComposing.setLength(0);
            	ComKbd.mComposing.append(text1+text2) ;
                Const.bFromKbd = true ; // encrypt/decrypt from keyboard
                if (Const.isLock())
                {
                    Const.saveDspPopup(Const.DspPasswordAsk);
                    Const.startPopup(Const.getService());
                }
                else {
                    ComKbd.commitEncryptDecryptTyped(ic);
                }
        }
        else if (primaryCode == LatinKeyboardView.KEYCODE_SETTINGS)
        {
            Const.saveDspPopup(Const.DspSettings) ;
            Const.startPopup(Const.getService()) ;
        }
        else
        {
        	if (primaryCode == LatinKeyboardView.KEYCODE_NUMERIC_SYMBOL)
            {
               	Korean.finishHangul(getCurrentInputConnection());

                if (current == mNumericSymbolsKeyboard)
                {
                    setLatinKeyboard(mSymbolsShiftedKeyboard);
                }
                else
                {
                    setLatinKeyboard(mNumericSymbolsKeyboard);
                }
            }
        	else if (primaryCode == LatinKeyboardView.KEYCODE_HAN_HAN)
            {
               	//Korean.finishHangul(getCurrentInputConnection());
        		if (Const1.getHangulKbdType() == Const1.HAN_QWERTY)
        		{
        			setLatinKeyboard(mChunjiinKeyboard);
        			Const1.setHangulKbdType(Const1.HAN_CHUNJIIN);
        		}
        		else if (Const1.getHangulKbdType() == Const1.HAN_CHUNJIIN)
        		{
        			setLatinKeyboard(mGoogleKeyboard);
        			Const1.setHangulKbdType(Const1.HAN_GOOGLE);
        		}
        		else
        		{
        			setLatinKeyboard(mQwertyKoreanKeyboard);
        			Const1.setHangulKbdType(Const1.HAN_QWERTY);
        		}
            }
            else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE // ��/��
                    && mInputView != null)
            {
            	handleKeycodeModeChange(getCurrentInputConnection()) ;
            }
            else if (primaryCode == Keyboard.KEYCODE_SHIFT)
            {
                handleShift();
                //updateShiftKeyState();//getCurrentInputEditorInfo());
                return ;
            }

            else if (primaryCode == 10) //ENTER
            {
           	    setAutoShift(true) ;

       			Korean.finishHangul(getCurrentInputConnection());

            	if (mCurKeyboard.mEnterIsCr)
            	{
                    handleCharacter(primaryCode, keyCodes);
            	}
            	else
            	{
                    keyDownUp(KeyEvent.KEYCODE_ENTER);
            	}
            }
            else if (isKoreanKeyboard())
        	{
            	Korean.checkHangulFinish(getCurrentInputConnection(), primaryCode) ;
            	if (ComKbd.mComposing.length() == 0 && !Korean.existTempHangul() &&
                		primaryCode == Keyboard.KEYCODE_DELETE)
                {
                    handleBackspace();
                }
                else
                {
        		   Korean.hangulMake(getCurrentInputConnection(), primaryCode);
        		   if (primaryCode >= 200)
        		   {
        		        if (mQwertyKoreanShiftKeyboard == current) // toggle
        		        {
        		        	setLatinKeyboard(mQwertyKoreanKeyboard);
        		            mInputView.setShifted(false);
        		        }

        		   }
                }
        	}
        	else if (isWordSeparator(primaryCode))
            {
       			Korean.finishHangul(getCurrentInputConnection());
                handleCharacter(primaryCode, keyCodes);

                // Handle separator
                if (/*(mQwertyKeyboard == current) &&*/ (primaryCode == ' ') &&
                		(Const1.prevCode == '.' ||
                		Const1.prevCode == '!' ||
                		Const1.prevCode == '?'))
                {
                	setAutoShift(true) ;
                }
            	Const1.prevCode = primaryCode ;
            }
            else if (primaryCode == Keyboard.KEYCODE_DELETE)
            {
       			Korean.finishHangul(getCurrentInputConnection());
                handleBackspace();
            }
            else if (primaryCode == Keyboard.KEYCODE_CANCEL)
            {
                handleClose();
                return;
            }
            else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS)
            {
                // Show a menu or somethin'
            }
            else
            {
                Korean.finishHangul(getCurrentInputConnection());
                handleCharacter(primaryCode, keyCodes);
                setAutoShift(false);
            }
        }

        Const1.prevCode = primaryCode ; //ysk20160502
        Korean.dspComposing(getCurrentInputConnection());
        dspAllGroupId() ;
        //mInputView.changeEncryptKey(mCurKeyboard, ComKbd.bEncrypted);

        /*
        if (ComKbd.bEncrypted &&
        		primaryCode != LatinKeyboardView.KEYCODE_LANGUAGE_ENCRYPT) //LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH)
        {
        	ComKbd.bEncrypted = false ;
        }
        */
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        ComKbd.mComposing.append(text); //ysk
        ic.endBatchEdit();
        //updateShiftKeyState();//getCurrentInputEditorInfo());
    }

	private void handleKeycodeModeChange(InputConnection ic)
	{
		Korean.finishHangul(ic);

	    Keyboard current = mInputView.getKeyboard();

	    if ((current == mChunjiinKeyboard) || (current == mQwertyKoreanKeyboard) ||
	    	(current == mGoogleKeyboard))
	    {
	    	Const1.EnglishMode = true ;
	        setLatinKeyboard(mQwertyKeyboard);
        }
		else if (current == mQwertyKeyboard) // english mode
		{
			Const1.EnglishMode = false ;

			// hangul
			switch (Const1.getHangulKbdType())
			{
			case Const1.HAN_CHUNJIIN :
	            setLatinKeyboard(mChunjiinKeyboard);
	            break;

			case Const1.HAN_GOOGLE :
	            setLatinKeyboard(mGoogleKeyboard);
	            break ;

			default :
	            setLatinKeyboard(mQwertyKoreanKeyboard);
			}
		}
		else // other mode
		{
		    if (Const1.EnglishMode)
			   setLatinKeyboard(mQwertyKeyboard);
		    else
		    {
	    		switch (Const1.getHangulKbdType())
	    		{
	    		case Const1.HAN_CHUNJIIN :
	                setLatinKeyboard(mChunjiinKeyboard);
	                break;

	    		case Const1.HAN_GOOGLE :
	                setLatinKeyboard(mGoogleKeyboard);
	                break ;

	    		default :
	                setLatinKeyboard(mQwertyKoreanKeyboard);
	    		}
		    }
		}
        updateShiftKeyState() ;
    }


    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    /*
    private void updateCandidates() {
        if (!mCompletionOn) {
            if (ComKbd.mComposing.length() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(ComKbd.mComposing.toString());
                setSuggestions(list, true, true);
            } else {
                setSuggestions(null, false, false);
            }
        }
    }
    */

    /*
    public void setSuggestions(List<String> suggestions, boolean completions,
            boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }
    */

    private void handleBackspace() {
        final int length = ComKbd.mComposing.length();
        if (length > 1)
        {
        	//ysk
            ComKbd.mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(ComKbd.mComposing, 1);
            //updateCandidates();
        } else if (length > 0) {
            ComKbd.mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            //updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        //updateShiftKeyState();//getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }

        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKoreanKeyboard == currentKeyboard)
        {
        	setLatinKeyboard(mQwertyKoreanShiftKeyboard);
            mInputView.setShifted(true);
        }
        else if (mQwertyKoreanShiftKeyboard == currentKeyboard)
        {
        	setLatinKeyboard(mQwertyKoreanKeyboard);
            mInputView.setShifted(false);
        }
        else if (mQwertyKeyboard == currentKeyboard)
        {
            // Alphabet keyboard
            mShiftState++ ;
            if (mShiftState > 2)
            	mShiftState = 0 ;

            mInputView.setShifted(mShiftState > 0);
        }
        else if (currentKeyboard == mNumericSymbolsKeyboard)
        {
        	mNumericSymbolsKeyboard.setShifted(true);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        }
        else if (currentKeyboard == mSymbolsShiftedKeyboard)
        {
            mSymbolsShiftedKeyboard.setShifted(false);
            setLatinKeyboard(mNumericSymbolsKeyboard);
            mNumericSymbolsKeyboard.setShifted(false);
        }
    }

    private void handleCharacter(int primaryCode, int[] keyCodes)
    {
        if (isInputViewShown())
        {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        // if (isAlphabet(primaryCode) && mPredictionOn) {
        //if ((ComKbd.isAlphabet(primaryCode) || isWordSeparator(primaryCode)) && mPredictionOn) {
            ComKbd.mComposing.append((char) primaryCode);
            //updateShiftKeyState();//getCurrentInputEditorInfo());
            //updateCandidates();
        /*
        } 
        else 
        {
            getCurrentInputConnection().commitText(
                    String.valueOf((char) primaryCode), 1);
        }
        */
    }

    private void handleClose() 
    {
    	Korean.finishHangul(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), false /* onlyCurrentIme */);
    }

    private void setAutoShift(boolean bSet)
    {
    	if (bSet)
    	{
        	if (mShiftState == 0)
                mShiftState = 1;
    	}
    	else
    	{
        	if (mShiftState == 1)
                mShiftState = 0;
    	}

        updateShiftKeyState() ;
        /*
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 < now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
        */
    }
    
    private String getWordSeparators() {
        return mWordSeparators;
    }
    
    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }
    
    public void pickSuggestionManually(int index) 
    {
        if (mCompletionOn && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
            //updateShiftKeyState();//getCurrentInputEditorInfo());
        } 
        else if (ComKbd.mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.
            //commitTyped(getCurrentInputConnection());
        }
    }
    
    public void swipeRight() {
        if (mCompletionOn) {
            pickDefaultCandidate();
        }
    }
    
    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }
    
    public void onPress(int primaryCode) {
    }
    
    public void onRelease(int primaryCode) {
    }
}
