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

/** A touch listener implementation that implements all callbacks by doing nothing.
 * You can extend this class and override only the methods you need.
 * 
 * Like usual for Java-Beans events.
 * 
 * @author alex
 */
public class TouchAdapter implements TouchListener{	
	
	public TouchAdapter(){}

	/** User has briefly tapped the surface with finger tip.
	 * @param e Describes the event.
	 * */
	@Override public void onClick(MouseEvent e){}

	/** User has tapped the surface with finger tip, for a longer time.
	 * How long must a click be to be a long click? Android system setting.
	 * @param e Describes the event.
	 * */
	@Override public void onLongClick(MouseEvent e){}

	/** User has tapped the surface twice in rapid succession, at (roughly) the same place.
	 * @param e Describes the event.
	 * */
	@Override public void onDoubleClick(MouseEvent e){}

	/** User moves finger over the surface without losing contact.
	 * 
	 * XXX What information do we need in the parameter?
	 * @param e The event. I will restrict its type.
	 * */
	@Override public void onDrag(DragEvent e){}

	/** User moves two fingers over the surface without losing contact.
	 * If the fingers approach each other, it's a pinch for zooming out.
	 * If the fingers become further apart, it's a spread for zooming in.
	 * @param e The event.
	 * */
	@Override public void onPinch(PinchEvent e){}

	/** User makes a long click with one finger and during the long click,
	 * taps another finger at a different point.
	 * 
	 * XXX What information do we need in the parameter?
	 * @param e The event. I will restrict its type.
	 * */
	@Override public void onPressAndClick(EventObject e){}

	/** User makes a long click with one finger and during the long click,
	 * drags another finger from one point to another.
	 * 
	 * XXX What information do we need in the parameter?
	 * @param e The event. I will restrict its type.
	 * */
	@Override public void onPressAndDrag(EventObject e){}

	/** User uses two fingers to describe a rotation on the screen.
	 * XXX This is not clearly defined.
	 * XXX What information do we need in the parameter?
	 * @param e The event. I will restrict its type.
	 * */
	@Override public void onRotate(EventObject e){}

}