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

/** A listener for some common touch screen gestures, as explained for example in http://www.lukew.com/ff/entry.asp?1071. 
 * In the class design, I follow the well-known Java-Beans and Java Swing and AWT more closely than the Android API. 
 * @author alex
 * */
public interface TouchListener{

	/** User has briefly tapped the surface with finger tip.
	 * @param e Describes the event.
	 * */
	void onClick(MouseEvent e);
	
	/** User has tapped the surface with finger tip, for a longer time.
	 * How long must a click be to be a long click? Android system setting.
	 * @param e Describes the event.
	 * */
	void onLongClick(MouseEvent e);
	
	/** User has tapped the surface twice in rapid succession, at (roughly) the same place.
	 * @param e Describes the event.
	 * */
	void onDoubleClick(MouseEvent e);
	
	/** User moves finger over the surface without losing contact.
	 * @param e The event. I will restrict its type.
	 * */
	void onDrag(DragEvent e);

	/** User moves finger over the surface without losing contact.
	 * @param e The event. I will restrict its type.
	 * */
	void onPinch(PinchEvent e);
	
	/** User moves finger over the surface without losing contact.
	 * @param e The event. I will restrict its type.
	 * */
	void onSpread(PinchEvent e);

	/** User makes a long click with one finger and during the long click,
	 * taps another finger at a different point.
	 * 
	 * XXX What information do we need in the parameter?
	 * @param e The event. I will restrict its type.
	 * */
	void onPressAndClick(EventObject e);
	
	/** User makes a long click with one finger and during the long click,
	 * drags another finger from one point to another.
	 * 
	 * XXX What information do we need in the parameter?
	 * @param e The event. I will restrict its type.
	 * */
	void onPressAndDrag(EventObject e);
	
	/** User uses two fingers to describe a rotation on the screen.
	 * XXX This is not clearly defined.
	 * XXX What information do we need in the parameter?
	 * @param e The event. I will restrict its type.
	 * */
	void onRotate(EventObject e);
}