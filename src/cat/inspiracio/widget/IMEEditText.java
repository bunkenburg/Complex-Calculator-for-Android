package cat.inspiracio.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.RelativeLayout;
import cat.inspiracio.calculator.R;

/** Like android.widget.EditText except that it can use a specific IME
 * instead of the one configured in the system. */
public class IMEEditText extends EditText{

	private static final String TAG="IMEEditText";
	
	//State ----------------------------------------------
	
	/** Use this IME instead of the one configured in the system. */
	private DirectInputMethodService ims;
	
	/** The input type the client asked for. */
	private int inputType;
	
	private boolean isKeyboardVisible=false;
	
	/** If a client sets a click listener, it goes here. */
	private OnClickListener clickListener;
	
	/** If a client sets a long click listener, it goes here. */
	private OnLongClickListener longClickListener;
	
	/** If a client sets an OnKeyListener, it goes here. */
	private OnKeyListener keyListener;
	
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
				if(longClickListener!=null)
					return longClickListener.onLongClick(v);
				return false;//not used the event
			}
		};
		this.setOnLongClickListener(ll);
		
		//Disable the system input method
		this.inputType=this.getInputType();
		this.setInputType(0);//none
	}
	
	//Accessors ------------------------------------------
	
	/** Sets the IME to use with this EditText. 
	 * Call once, before anything else.
	 * Wraps the input method service and overrides some of its methods. */
	public final void setInputMethodService(DirectInputMethodService ims){
		Context c=this.getContext();
		ims.setContext(c);
		this.ims=ims;
		this.ims.onCreate();
		this.ims.onInitializeInterface();
	}

	@Override public final void setOnClickListener(OnClickListener l){this.clickListener=l;}
	
	@Override public final void setOnKeyListener(OnKeyListener l){
		super.setOnKeyListener(l);
		this.keyListener=l;
	}
	
	public final OnKeyListener getOnKeyListener(){return this.keyListener;}
	
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
		//Everything in the activity, but shrunk, and the keyboard's input view.
		Context context=this.getContext();// gives the activity.
		Activity activity=(Activity)context;
		
		View top=activity.findViewById(R.id.top);//Generalise this
		//Remove top from its parent
		ViewGroup parent=(ViewGroup)top.getParent();//FrameLayout extends ViewGroup
		parent.removeView(top);
		
		//Relative layout
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout all=(RelativeLayout)inflater.inflate(R.layout.activity_with_kb, null);
		all.setGravity(Gravity.FILL);
		int width=ViewGroup.LayoutParams.FILL_PARENT;
		int height=ViewGroup.LayoutParams.WRAP_CONTENT;
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(width, height);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		all.addView(inputView, params);

		width=ViewGroup.LayoutParams.FILL_PARENT;
		height=ViewGroup.LayoutParams.FILL_PARENT;
		params=new RelativeLayout.LayoutParams(width, height);
		int id=inputView.getId();
		params.addRule(RelativeLayout.ABOVE, id);
		all.addView(top, params);
		
		params=new RelativeLayout.LayoutParams(width, height);
		parent.addView(all, params);
		
		isKeyboardVisible=true;
	}
	
	/** Hides the keyboard. Must undo what showKeyboard did. */
	private void hideKeyboard(){
		if(!this.isKeyboardVisible)
			return;
		/// Tell the IMS it will be hidden
		boolean finishingInput=true;
		this.ims.onFinishInputView(finishingInput);
		
		Context context=this.getContext();// gives the activity.
		Activity activity=(Activity)context;
		ViewGroup all=(ViewGroup)activity.findViewById(R.id.all);
		
		//Remove all from its parent
		ViewGroup parent=(ViewGroup)all.getParent();
		parent.removeView(all);
		
		//Put just top there
		View top=all.findViewById(R.id.top);
		all.removeView(top);
		parent.addView(top);
		
		this.isKeyboardVisible=false;
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
	@Override public final boolean onCheckIsTextEditor(){return false;}
		
	/** Listens to BACK: if the keyboard is visible, close it.
	 * Else delegate to super.
	 * @see android.widget.TextView#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override public final boolean onKeyDown(int keyCode, KeyEvent event){
		int repeatCount=event.getRepeatCount();
	    if(keyCode==KeyEvent.KEYCODE_BACK && repeatCount==0 && this.isKeyboardVisible){
	    	this.hideKeyboard();
	        return true;
	    }
	    boolean b=super.onKeyDown(keyCode, event);
	    return b;
	}

	//Helpers -------------------------------------------
	
	@SuppressWarnings("unused")
	private void say(Object o){Log.d(TAG, o.toString());}
}