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
package inspiracio.view;

import java.util.EventObject;

/** Imitates java.awt.events.MouseEvent for Android. 
 * @author alex
 * */
public final class MouseEvent extends EventObject{
	
	//State ------------------------------------
	
	/** The position of the click. Android uses floats for this. */
	private float x, y;
	
	//Constructor ------------------------------
	
	/** Makes a new mouse event.
	 * I think constructors with many parameters are confusing, especially if the
	 * parameters are of primitive types. Therefore, use setters.
	 * @param source The source of the event, usually the view on which it occurred. */
	MouseEvent(Object source){
		super(source);
	}

	//Accessors --------------------------------
	
	public float getX(){return x;}
	public void setX(float x){this.x=x;}

	public float getY(){return y;}
	public void setY(float y){this.y=y;}
}