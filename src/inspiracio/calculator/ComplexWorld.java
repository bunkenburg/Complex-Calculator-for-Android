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

import java.text.ParseException;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

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
	
	public ComplexWorld(){}
	
	//Activity methods ------------------------------------------------------------
	
    /** Called when the activity is first created. */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        
        this.display=(EditText)this.findViewById(R.id.display);
        display.setOnKeyListener(new OnKeyListener(){
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
    }
    
    //Helpers ----------------------------------------------------------------
    
    /** Gets the expression from the display, parses it, evaluates it,
     * and append the result to the display. */
	private void doEquals(){
        Editable editable=display.getText();
        String s=editable.toString();
        display.append(" = ");
        try{
            SyntaxTree tree=SyntaxTree.parse(s);
            EC ec=tree.evaluate(null);
            display.append(ec.toString());
            if(this.world!=null)
            	world.add(ec);
            return;
        }catch(BugException bugexception){
        	bugexception.printStackTrace();
        }catch(PartialException partialexception){
        	partialexception.printStackTrace();
        }catch(ParseException parseexception){
        	parseexception.printStackTrace();
        }
    }

}