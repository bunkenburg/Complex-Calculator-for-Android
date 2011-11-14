package inspiracio.widget;

import inspiracio.calculator.R;
import inspiracio.calculator.SoftKeyboard;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.RelativeLayout;

/** Like android.widget.EditText except that it can use a specific IME
 * instead of the one configured in the system. */
public class IMEEditText extends EditText{

	private static final String TAG="IMEEditText";
	
	//State ----------------------------------------------
	
	/** Use this IME instead of the one configured in the system. */
	private SoftKeyboard ims;
	
	/** The input type the client asked for. */
	private int inputType;
	
	private boolean isKeyboardVisible=false;
	
	//Constructors ---------------------------------------
	
	public IMEEditText(Context context){
		super(context);
		init();
	}
	
	/** Called by inflation */
	public IMEEditText(Context context, AttributeSet attrs){
		super(context, attrs);
		init();
	}
	
	public IMEEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		OnClickListener l=new OnClickListener(){
			@Override public void onClick(View v){
				say("onClick " + v);
				IMEEditText.this.onClick();
			}
		};
		this.setOnClickListener(l);
		OnLongClickListener ll=new OnLongClickListener(){
			@Override public boolean onLongClick(View v){
				say("onLongClick " + v);
				return true;
			}
		};
		this.setOnLongClickListener(ll);
		
		//Disable the system input method
		this.inputType=this.getInputType();
		this.setInputType(0);//none
		//this.setEnabled(false);//Makes the edit text all grey.
	}
	
	//Accessors ------------------------------------------
	
	/** Sets the IME to use with this EditText. 
	 * Call once, before anything else. */
	public final void setInputMethodService(SoftKeyboard ims){
		this.ims=ims;
		Context c=this.getContext();
		this.ims.setContext(c);
		this.ims.onInitializeInterface();
	}

	//Helpers --------------------------------------------
	
	/** User has clicked in the edit text. 
	 * Show keyboard if necessary, and set cursor. */
	private void onClick(){
		showKeyboard();
		//Must I set cursor?
	}
	
	/** Show the keyboard. */
	private void showKeyboard(){
		if(isKeyboardVisible)
			return;
		//Show the keyboard
		View inputView=this.ims.onCreateInputView();
		//Make a vertical relative layout with two subviews:
		//Everything in the activity, but shrunk,
		//and the keyboard's view.
		Context context=getContext();// gives the activity.
		Activity activity=(Activity)context;
		//Window window=activity.getWindow();
		//From the activity, we can get the window. From there, we can remove everything, shrink it, and put it back, above a keyboard.
		View top=activity.findViewById(R.id.top);//Generalise this
		RelativeLayout rl=(RelativeLayout)top;//Generalise this. I know that main.xml defines a vertical relative layout.
		rl.addView(inputView);//XXX Add it below, not at the above everything else.
		
		isKeyboardVisible=true;
	}	
	
	//Overridden methods --------------------------------------------
	
	/** Called by InputMethodManager.startInputInner(), InputMethodManager.checkFocus(), InputMethodManager.onWindowFocus(View, View, int, boolean, int). Also called by IMM.startInputInner(), IMM$H.handleMessage(Message)
	 * @param info
	 * @see android.widget.TextView#onCreateInputConnection(android.view.inputmethod.EditorInfo)
	 */
	@Override public final InputConnection onCreateInputConnection(EditorInfo info){
		InputConnection ic=null;//super.onCreateInputConnection(info);//Returns null if onCheckIsTextEditor returns false. Assigns null, so we don't need to call it.
		IMEEditText targetView=this;
		boolean fullEditor=true;
		DirectInputConnection dic=new DirectInputConnection(targetView, fullEditor);
		dic.setInputMethodService(ims);
		dic.setInputType(inputType);
		ic=dic;
		return ic;
	}

	/** What exactly is this used for? 
	 * This is a text editor, but I don't want the system to display a soft keyboard. 
	 * 
	 * Called from TextView.isTextEditable, TextView.makeBlink(), TextView.onFocusChanged(boolean, int, Rect), View.handleFocusGainInternal(int, Rect) 
	 * Called also from super.onCreateInputConnection(EditorInfor).
	 * Also called from InputMethod Manager.onWindowFocus(View, View, int, boolean, int), ViewRoot.handleMessage(Message).
	 * */
	@Override public final boolean onCheckIsTextEditor(){
		return false;
	}
	
	//Helpers -------------------------------------------
	
	void say(Object o){
		Log.d(TAG, o.toString());
	}
}