package cat.inspiracio.widget;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.InputConnection;

/** Implement this to make an input method service that can be called directly. */
public abstract class DirectInputMethodService extends InputMethodService{
	
	//State -----------------------------------

    /** The context to use. */
    private Context context;
    
    /** The input connection to use. */
    private InputConnection ic;
    
    //Constructors ----------------------------
    
    protected DirectInputMethodService(){}
	
	//Accessors -------------------------------
	
    public final void setContext(Context context){this.context=context;}
    public final Context getContext(){return this.context;}
    
    public final void setInputConnection(InputConnection ic){this.ic=ic;}
    @Override public final InputConnection getCurrentInputConnection(){
    	if(this.ic!=null)
    		return this.ic;//for direct use
    	return super.getCurrentInputConnection();//for real use
    }

	//Methods ---------------------------------

}