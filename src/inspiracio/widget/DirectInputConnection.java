package inspiracio.widget;

import android.view.View;
import android.view.inputmethod.BaseInputConnection;

/** An input connection that connects directly to the IME. */
class DirectInputConnection extends BaseInputConnection {

	//State --------------------------------------------------
	
	private View targetView;
	private boolean fullEditor;
	
	//Constructors -------------------------------------------
	
	DirectInputConnection(View targetView, boolean fullEditor) {
		super(targetView, fullEditor);
	}

	//Methods -----------------------------------------------
	
}