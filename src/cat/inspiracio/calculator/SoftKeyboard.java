/*	Copyright 2011 Alexander Bunkenburg alex@inspiracio.com
 * 
 * This file is part of Complex Calculator for Android.
 * 
 * Complex Calculator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Complex Calculator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Complex Calculator for Android. If not, see <http://www.gnu.org/licenses/>.
 * */

package cat.inspiracio.calculator;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import cat.inspiracio.calculator.keyboard.CandidateView;
import cat.inspiracio.calculator.keyboard.CCKeyboard;
import cat.inspiracio.calculator.keyboard.CCKeyboardView;
import cat.inspiracio.widget.DirectInputMethodService;

/** Made specially to be called directly, not as service.
 * But the only change is the superclass. */
public final class SoftKeyboard extends DirectInputMethodService implements KeyboardView.OnKeyboardActionListener{
    
	/** This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on 
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
    private static final boolean PROCESS_HARD_KEYS=true;
    
    //State ----------------------------------------------------
    
    private KeyboardView mInputView;
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;
    
    private StringBuilder mComposing=new StringBuilder();
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;
    
    private CCKeyboard mQwertyKeyboard;
    private CCKeyboard mCurKeyboard;
    private String mWordSeparators;

    //Constructor ------------------------------------------------
    
    public SoftKeyboard(){}
    
    //Accessors --------------------------------------------------    
    
    @Override public LayoutInflater getLayoutInflater(){
    	Context context=this;//this.getContext();
    	Object o=context.getSystemService(LAYOUT_INFLATER_SERVICE);
    	LayoutInflater l=(LayoutInflater)o;
    	return l;
    }
    
    //Life cycle -------------------------------------------------
    
    /** Main initialisation of the input method component. Be sure to call to super class. */
    @Override public void onCreate(){
        super.onCreate();
        Resources resources=this.getResources();
        mWordSeparators=resources.getString(R.string.word_separators);
    }
    
    /** This is the point where you can do all of your UI initialisation.  It
     * is called after creation and any configuration change. */
    @Override public void onInitializeInterface(){
    	Resources resources=this.getResources();
        mWordSeparators=resources.getString(R.string.word_separators);
        if(mQwertyKeyboard!=null){
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        Context context=this;
        mQwertyKeyboard=new CCKeyboard(context, R.xml.qwerty);
    }
    
    /** Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change. */
    @Override public final View onCreateInputView(){
    	LayoutInflater inflater=this.getLayoutInflater();
        View v=inflater.inflate(R.layout.input, null);
        mInputView=(KeyboardView)v;
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setKeyboard(mQwertyKeyboard);
        return mInputView;
    }

    /** Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}. */
    @Override public View onCreateCandidatesView(){
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        return mCandidateView;
    }

    /** This is the main point where we do our initialisation of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits. */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting){
        super.onStartInput(attribute, restarting);
        
        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);
        updateCandidates();
        
        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }
        
        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;
        
        mCurKeyboard = mQwertyKeyboard;
        updateShiftKeyState(attribute);
        
        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    /** This is called when the user is done editing a field.  We can use
     * this to reset our state. */
    @Override public void onFinishInput(){
        super.onFinishInput();
        
        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateCandidates();
        
        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);
        
        mCurKeyboard = mQwertyKeyboard;
        if(mInputView!=null)
            mInputView.closing();
    }
    
    @Override public void onStartInputView(EditorInfo attribute, boolean restarting){
        super.onStartInputView(attribute, restarting);
        // Apply the selected keyboard to the input view.
        mInputView.setKeyboard(mCurKeyboard);
        mInputView.closing();
    }
    
    /** Deal with the editor reporting movement of its cursor. */
    @Override public final void onUpdateSelection(int oldSelStart, int oldSelEnd,int newSelStart, int newSelEnd,int candidatesStart, int candidatesEnd){
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        
        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null)
                ic.finishComposingText();
        }
    }

    /** This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation. */
    @Override public void onDisplayCompletions(CompletionInfo[] completions){
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }
            
            List<String> stringList = new ArrayList<String>();
            for (int i=0; i<(completions != null ? completions.length : 0); i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }
    
    /** This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option. */
    private boolean translateKeyDown(int keyCode, KeyEvent event){
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null)
            return false;
        
        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }
        
        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() -1 );
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length()-1);
            }
        }
        
        onKey(c, null);
        return true;
    }
    
    /** Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app. */
    @Override public final boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack())
                        return true;
                }
                break;
                
            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of letting the application to the delete itself.
                if(0<mComposing.length()){
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
                    if (mPredictionOn && translateKeyDown(keyCode, event))
                        return true;
                }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /** Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app. */
    @Override public final boolean onKeyUp(int keyCode, KeyEvent event) {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS)
            if (mPredictionOn)
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState, keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    /** Helper function to commit any text being composed in to the editor. */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
            updateCandidates();
        }
    }

    /** Helper to update the shift state of our keyboard based on the initial
     * editor state. */
    private void updateShiftKeyState(EditorInfo attr) {
        if(attr!=null && mInputView!=null && mQwertyKeyboard==mInputView.getKeyboard()){
            int caps=0;
            EditorInfo ei=getCurrentInputEditorInfo();
            if(ei!=null && ei.inputType!=EditorInfo.TYPE_NULL)
                caps=getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            mInputView.setShifted(mCapsLock || caps!=0);
        }
    }
    
    /** Helper to determine if a given character code is alphabetic. */
    private boolean isAlphabet(int code){return Character.isLetter(code);}
    
    /** Helper to send a key down / key up pair to the current editor. */
    private void keyDownUp(int keyEventCode){
    	InputConnection ic=this.getCurrentInputConnection();
    	KeyEvent e=new KeyEvent(ACTION_DOWN, keyEventCode);
        ic.sendKeyEvent(e);
        e=new KeyEvent(ACTION_UP, keyEventCode);
        ic.sendKeyEvent(e);
    }
    
    /** Helper to send a character to the editor as raw key events. */
    private void sendKey(int keyCode){
        switch (keyCode) {
            case '\n':
                this.keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9')
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                else
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                break;
        }
    }

    // Implementation of KeyboardViewListener

    @Override public final void onKey(int primaryCode, int[] keyCodes){
        if(isWordSeparator(primaryCode)){
            // Handle separator
            if(0<mComposing.length())
                commitTyped(getCurrentInputConnection());
            
            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
        } 
        else if(primaryCode==Keyboard.KEYCODE_DELETE)
            handleBackspace();
        
        else if (primaryCode == Keyboard.KEYCODE_SHIFT)
            handleShift();
        
        else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } 
        else if (primaryCode == CCKeyboardView.KEYCODE_OPTIONS) {
            // Show a menu or something
        } 
        else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            current = mQwertyKeyboard;
            mInputView.setKeyboard(current);
        } 
        else
            handleCharacter(primaryCode, keyCodes);

    }

    public final void onText(CharSequence text){
        InputConnection ic=getCurrentInputConnection();
        if(ic==null)
        	return;
        ic.beginBatchEdit();
        if(0<mComposing.length())
            commitTyped(ic);
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    /** Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates. */
    private void updateCandidates(){
        if(!mCompletionOn){
            if(0<mComposing.length()){
                ArrayList<String> list=new ArrayList<String>();
                list.add(mComposing.toString());
                setSuggestions(list, true, true);
            }
            else
                setSuggestions(null, false, false);
        }
    }
    
    public final void setSuggestions(List<String> suggestions, boolean completions, boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0)
            setCandidatesViewShown(true);
        else if (isExtractViewShown())
            setCandidatesViewShown(true);
        if (mCandidateView != null)
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
    }
    
    private void handleBackspace(){
        final int length=mComposing.length();
        if(1<length){
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        }
        else if(0<length){
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        }
        else
            keyDownUp(KeyEvent.KEYCODE_DEL);
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if(mInputView==null)
            return;
        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        } 
    }
    
    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (mInputView.isShifted())
                primaryCode = Character.toUpperCase(primaryCode);
        }
        if (isAlphabet(primaryCode) && mPredictionOn) {
            mComposing.append((char) primaryCode);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateShiftKeyState(getCurrentInputEditorInfo());
            updateCandidates();
        }
        else
            getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
    }

    private void handleClose(){
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }

    private void checkToggleCapsLock(){
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else
            mLastShiftTime = now;
    }
    
    private String getWordSeparators(){return mWordSeparators;}
    
    public final boolean isWordSeparator(int code){
        String separators=this.getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }

    public final void pickDefaultCandidate(){pickSuggestionManually(0);}
    
    public final void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0 && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null)
                mCandidateView.clear();
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.
            commitTyped(getCurrentInputConnection());
        }
    }
    
    public final void swipeRight(){
        if (mCompletionOn)
            pickDefaultCandidate();
    }
    
    public final void swipeLeft(){handleBackspace();}

    public final void swipeDown(){handleClose();}

    public final void swipeUp(){}
    
    public final void onPress(int primaryCode){}
    
    public final void onRelease(int primaryCode){}
}