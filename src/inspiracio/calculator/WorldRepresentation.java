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

import inspiracio.numbers.EC;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

/** Some representation where complex numbers can be shown. */
abstract class WorldRepresentation extends View{
	
	//State -----------------------------------------------------------------------------
	
	protected ComplexWorld calculator;
	
	//Constructors ----------------------------------------------------------------------
	
	/** Required for instantiation in code */
	protected WorldRepresentation(Context ctx){
		super(ctx);
	}
	
	/** Inflation from resource file */
	protected WorldRepresentation(Context ctx, AttributeSet ats){
		super(ctx, ats);
	}
	
	/** Inflation from resource file */
	protected WorldRepresentation(Context ctx, AttributeSet ats, int defaultStyle){
		super(ctx, ats, defaultStyle);
	}
	
	//Methods ---------------------------------------------------------------------------

	/** Adds a number to be displayed in the world. */
	abstract void add(EC c);
	
	/** Clears all displayed numbers and stuff. */
	abstract void clear();
	
    /** Draws the stuff that the world should show. */
    abstract void drawStuff(Drawing drawing);

	/** Resets to centre on zero. */
    abstract void reset();
    
    void set(ComplexWorld calculator){this.calculator=calculator;}

    //Helpers --------------------------------------------------------------------------

	abstract void parcel(String prefix, Bundle b);
	abstract void unparcel(String prefix, Bundle b);

}