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
package inspiracio.calculator;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.KeyEvent.KEYCODE_EQUALS;
import inspiracio.numbers.BugException;
import inspiracio.numbers.EC;
import inspiracio.numbers.PartialException;
import inspiracio.parsing.SyntaxTree;
import inspiracio.widget.IMEEditText;

import java.text.ParseException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/** The activity for calculation. */
public final class ComplexWorld extends Activity{

	//State -----------------------------------------------------------------------
	
	/** Button for resetting, that is re-centering the plane. */
	private Button resetButton;
	
	/** Button for clearing, that is deleting all the shown numbers. */
	private Button clearButton;
	
	/** The world where the numbers are displayed graphically. */
	private WorldRepresentation world;

	/** The text box where the expression is displayed. */
	private EditText display;
	
	//Constructors ----------------------------------------------------------------
	
	public ComplexWorld(){
		//Make sure the class is loaded before inflating,
		//since the EditText references that class.
		//XXX Doesn't work. Find out how I can specify that
		//CC should use its own InputMethodService.
		Class<SoftKeyboard> c=SoftKeyboard.class;
		System.out.println(c);
	}
	
	//Activity methods ------------------------------------------------------------
	
    /** Called when the activity is first created. */
    @Override public void onCreate(Bundle bundle){
    	super.onCreate(bundle);
        this.setContentView(R.layout.main);
        
        this.resetButton=(Button)this.findViewById(R.id.resetButton);
        this.resetButton.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){world.reset();}
		});
        
        this.clearButton=(Button)this.findViewById(R.id.clearButton);
        this.clearButton.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){world.clear();}
		});
        
        this.world=(WorldRepresentation)this.findViewById(R.id.canvas);
        this.world.set(this);
        
        IMEEditText it=(IMEEditText)this.findViewById(R.id.display);
        InputMethodService ims=new SoftKeyboard();
        it.setInputMethodService(ims);
        this.display=it;
        
        /*
        //Identify the default IME
        ContentResolver cr=this.getContentResolver();
        String id=Settings.Secure.getString(cr,Settings.Secure.DEFAULT_INPUT_METHOD);

        //Suggest the complex calculator keyboard to the user.
        InputMethodManager imm=(InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);
        List<InputMethodInfo>imis=imm.getInputMethodList();//All the IMEs you have installed. 7 InputMethodInfo.
        boolean b=false;
        InputMethodInfo imi=null;
        for(InputMethodInfo i : imis){
        	String p=i.getPackageName();
        	if("inspiracio.calculator".equals(p)){
        		b=true;
        		imi=i;
        	}
        }
        //If CC is not in there, suggest putting it there. (?)
        imis=imm.getEnabledInputMethodList();//All the IMEs you have enabled. 4 InputMethodInfo
        b=false;
        imi=null;
        for(InputMethodInfo i : imis){
        	String p=i.getPackageName();
        	if("inspiracio.calculator".equals(p)){
        		b=true;
        		imi=i;
        	}
        }
        //If CC is not in there, suggest enabling it.
        //Can I find out which is the currently selected input method?
        //If it's not CC, show picker? Or is that too pushy?
        //imm.showInputMethodPicker();
        */
        
        this.display.setOnKeyListener(new OnKeyListener(){
            @Override public boolean onKey(View v, int keyCode, KeyEvent event){
                // If the event is a key-down event on the "enter" button
            	int action=event.getAction();
                if(action==ACTION_DOWN){
                	//Gets the expression, calculates the result, and adds it.
                	if(keyCode==KEYCODE_ENTER || keyCode==KEYCODE_EQUALS){
                		doEquals();
                		return true;
                	}
                }
                return false;
            }
        });
        //hides the input method
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //The text size of the display if 27.
        //float t=display.getPaint().getTextSize();//27
        //if(bundle!=null)this.world.onRestoreInstanceState(bundle);
    }

    /** Writes the state to bundle. */
	@Override protected final void onSaveInstanceState(Bundle bundle){
		super.onSaveInstanceState(bundle);
		this.world.parcel("inspiracio.calculator.world", bundle);
	}

	@Override protected final void onRestoreInstanceState(Bundle bundle){
		super.onRestoreInstanceState(bundle);
		this.world.unparcel("inspiracio.calculator.world", bundle);
	}

    //Methods ----------------------------------------------------------------
    
	/** Adds a complex number to the display. */
    final void add(EC ec){
    	int start=display.getSelectionStart();
    	int end=display.getSelectionEnd();
    	Editable editable=display.getEditableText();
    	editable.replace(start, end, "(" + ec + ")");
    }

    //Helpers ----------------------------------------------------------------
    
    /** Gets the expression from the display, parses it, evaluates it,
     * and append the result to the display. */
	private void doEquals(){
        Editable editable=display.getText();
        String s=editable.toString();
        display.append(" = ");
        String msg=null;
        try{
            SyntaxTree tree=SyntaxTree.parse(s);
            EC ec=tree.evaluate(null);
            display.append(ec.toString());
            if(this.world!=null)
            	world.add(ec);
            return;
        }catch(BugException be){
        	be.printStackTrace();
        	msg=be.getLocalizedMessage();
        }catch(PartialException pe){
        	pe.printStackTrace();
        	msg=pe.getLocalizedMessage();
        	msg="Undefined: " + msg;
        }catch(ParseException pse){
        	pse.printStackTrace();
        	msg=pse.getLocalizedMessage();
        }
        if(msg!=null){
        	Context context = getApplicationContext();
        	Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        	toast.show();
        }
    }

}