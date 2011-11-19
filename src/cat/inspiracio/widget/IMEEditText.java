package cat.inspiracio.widget;

import cat.inspiracio.calculator.SoftKeyboard;
import inspiracio.calculator.R;
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
	
	/** If a client sets a click listener, it goes here. */
	private OnClickListener clickListener;
	
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
			/** I want the click. I'll use it, and delegate it to a listener the client set. */
			@Override public void onClick(View v){IMEEditText.this.onClick(v);}
		};
		super.setOnClickListener(l);
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
	 * Call once, before anything else.
	 * Wraps the input method service and overrides some of its methods. */
	public final void setInputMethodService(SoftKeyboard ims){
		//XXX wrap
		this.ims=ims;
		Context c=this.getContext();
		this.ims.setContext(c);
		//this.ims.onCreate();//Should call this, but super.onCreate() fails because its context is no good.
		this.ims.onInitializeInterface();
	}

	@Override public void setOnClickListener(OnClickListener l){
		this.clickListener=l;
	}
	
	//Helpers --------------------------------------------
	
	/** User has clicked in the edit text:
	 * first call any listener the client has set, and then 
	 * show keyboard if necessary, and set cursor. */
	private void onClick(View v){
		if(this.clickListener!=null)
			this.clickListener.onClick(v);
		this.showKeyboard();
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
		//Add it below, not at the above everything else.
		int i=rl.getChildCount();
		rl.addView(inputView,i);
		
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