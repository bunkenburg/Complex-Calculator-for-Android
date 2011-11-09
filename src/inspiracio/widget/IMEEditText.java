package inspiracio.widget;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/** Like android.widget.EditText except that it can use a specific IME
 * instead of the one configured in the system. */
public class IMEEditText extends EditText{

	private static final String TAG="IMEEditText";
	
	//State ----------------------------------------------
	
	/** Use this IME instead of the one configured in the system. */
	private InputMethodService ims;
	
	/** The input type the client asked for. */
	private int inputType;
	
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
	public final void setInputMethodService(InputMethodService ims){
		this.ims=ims;
		
		//Tell the superclass that it's not editable: we do all the editing.
		//super.set
	}
	
	//Methods --------------------------------------------
	
	/** @see android.widget.TextView#onCreateInputConnection(android.view.inputmethod.EditorInfo)
	 */
	@Override public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		InputConnection ic=super.onCreateInputConnection(outAttrs);//Called by InputMethodManager.startInputInner()
		return ic;
	}

	/** What exactly is this used for? 
	 * This is a text editor, but I don't want the system to display a soft keyboard. */
	@Override public boolean onCheckIsTextEditor(){
		return false;
	}
	
	//Helpers -------------------------------------------
	
	void say(Object o){
		Log.d(TAG, o.toString());
	}
}