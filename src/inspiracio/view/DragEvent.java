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

/** Reported for a drag event. 
 * Reports the start and end positions. 
 * (Android's MotionEvent may deliver some historical positions in-between,
 * but I ignore them for now.)
 * */
public class DragEvent extends EventObject{

	//State ------------------------------------
	
	/** The start position. */
	private float startX, startY;
	
	/** The start position. */
	private float endX, endY;
		
	//Constructor ------------------------------
	
	/** Makes a new mouse event.
	 * I think constructors with many parameters are confusing, especially if the
	 * parameters are of primitive types. Therefore, use setters.
	 * @param source The source of the event, usually the view on which it occurred. */
	DragEvent(Object source){
		super(source);
	}

	//Accessors --------------------------------
	
	public float getStartX(){return startX;}
	public void setStartX(float x){this.startX=x;}
	public void setStart(float x, float y){this.startX=x; this.startY=y;}

	public float getStartY(){return startY;}
	public void setStartY(float y){this.startY=y;}
	
	public float getEndX(){return endX;}
	public void setEndX(float x){this.endX=x;}
	public void setEnd(float x, float y){this.endX=x; this.endY=y;}

	public float getEndY(){return endY;}
	public void setEndY(float y){this.endY=y;}
	
	@Override public String toString(){
		return "DragEvent[(" + startX + "," + startY + ") -> (" + endX + "," + endY + ")]";
	}
}