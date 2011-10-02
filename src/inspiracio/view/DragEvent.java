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

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import android.graphics.Point;

/** Reported for a drag event. 
 * Reports the start and end positions. 
 * (Android's MotionEvent may deliver some historical positions in-between,
 * but I ignore them for now.)
 * */
public class DragEvent extends EventObject{

	//State ------------------------------------

	private List<Float>xs;
	private List<Float>ys;
	
	//Constructor ------------------------------
	
	/** Makes a new mouse event.
	 * I think constructors with many parameters are confusing, especially if the
	 * parameters are of primitive types. Therefore, use setters.
	 * @param source The source of the event, usually the view on which it occurred. */
	DragEvent(Object source){
		super(source);
		this.xs=new ArrayList<Float>();
		this.ys=new ArrayList<Float>();
	}

	//Accessors --------------------------------

	void addPoint(float x, float y){this.xs.add(x);this.ys.add(y);}
	
	public float getStartX(){return this.xs.get(0);}
	public float getStartY(){return this.ys.get(0);}
	
	public float getEndX(){return this.xs.get(this.xs.size()-1);}
	public float getEndY(){return this.ys.get(this.ys.size()-1);}
	
	/** Returns a list of points. */
	public List<Point>getPoints(){
		//Maybe design this method better. Here we instantiate points.
		List<Point>points=new ArrayList<Point>();
		for(int i=0; i<xs.size(); i++){
			Point point=new Point();
			point.x=(int)(float)xs.get(i);
			point.y=(int)(float)ys.get(i);
			points.add(point);
		}
		return points;
	}
	
	@Override public String toString(){
		return "DragEvent[(" + getStartX() + "," + getStartY() + ") -> (" + getEndX() + "," + getEndY() + ")]";
	}
}