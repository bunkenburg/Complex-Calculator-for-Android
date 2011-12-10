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
package cat.inspiracio.calculator.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;

public final class CCKeyboardView extends KeyboardView{

    public static final int KEYCODE_OPTIONS = -100;
    
    //State --------------------------------------------------
    
    //Constructors -------------------------------------------
    
    /** Called by Inflater. */
    public CCKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CCKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //Callbacks --------------------------------------------
    
    @Override protected boolean onLongPress(Key key){
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL){
            OnKeyboardActionListener l=getOnKeyboardActionListener();
            l.onKey(KEYCODE_OPTIONS, null);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }
}
