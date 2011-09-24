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

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/** An object that can receive the touch events from Android's API and transform them 
 * common gesture calls, for client programmer convenience.
 * 
 * <h3>usage</h3>
 * Make your touch listener and register it with a touch dispatcher:
 * <pre>
 * 	TouchListener listener=new TouchAdapter(){
 * 		//Override the methods you need
 * 	};
 * 	TouchDispatcher dispatcher=newTouchDispatcher();
 * 	dispatcher.addTouchListener(listener);
 * </pre>
 * 
 * Then let your view's onTouchEvent() call the dispatcher:
 * <pre>
 * 	public void onTouchEvent(MotionEvent e){
 * 		dispatcher.onTouchEvent(this, e);
 * 	}
 * </pre>
 * 
 * Or set the dispatcher as the OnTouchListener of the view:
 * <pre>
 * 	view.setOnTouchListener(dispatcher);
 * </pre>
 * 
 * <h3>implemented</h3>
 * These callbacks are implemented:
 * <ul>
 * 	<li><strike>onClick</strike>
 * 	<li><strike>onDoubleClick</strike>
 * 	<li><strike>onDrag</strike>
 * 	<li><strike>onLongClick</strike>
 * 	<li><strike>onPinch</strike>
 * 	<li><strike>onPressAndClick</strike>
 * 	<li><strike>onPressAndDrag</strike>
 * 	<li><strike>onRotate</strike>
 * 	<li><strike>onSpread</strike>
 * </ul>
 * 
 * @author alex
 */
public final class TouchDispatcher implements OnTouchListener{

	//State ---------------------------------------------------

	/** The registered touch listeners. 
	 * Synchronise on this list before changing it. */
	private List<TouchListener>listeners;
	
	/** A touch listener that distributes the calls to
	 * all registered listeners. */
	private TouchListener multiplexer;
	
	//Constructor ---------------------------------------------
	
	public TouchDispatcher(){
		this.listeners=new ArrayList<TouchListener>();
		
		//Makes the multiplexer.
		this.multiplexer=new TouchListener(){
			
			@Override public void onClick(MouseEvent e){
				//Must copy them into an array in case the event adds or removes a listener
				//and thereby causes concurrent modification.
				TouchListener[]fix=new TouchListener[0];
				synchronized(listeners){
					fix=listeners.toArray(fix);
				}
				for(TouchListener t : fix)
					t.onClick(e);//I hope they are fast and none of them throws exception.
			}
			
			@Override public void onLongClick(MouseEvent e){throw new RuntimeException("not implemented");}
			@Override public void onDoubleClick(MouseEvent e){throw new RuntimeException("not implemented");}
			@Override public void onDrag(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onPinch(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onSpread(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onPressAndClick(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onPressAndDrag(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onRotate(EventObject e){throw new RuntimeException("not implemented");}
		};
	}
	
	//interface OnTouchListener -------------------------------
	
	/** Distinguishes the events we implement and calls the listeners. */
	@Override public final boolean onTouch(View view, MotionEvent e){
		//Only clicks so far.
		MouseEvent me=new MouseEvent(view);
		me.setX(e.getX());
		me.setY(e.getY());
		this.multiplexer.onClick(me);
		return true;
	}

	//Manage registered listeners ----------------------------
	
	/** Registers a touch listener. If you register it twice, it will receive the 
	 * callbacks twice. No guarantees about order of callbacks among several listeners. 
	 * @param listener
	 * */
	public final void addTouchListener(TouchListener listener){
		synchronized(this.listeners){
			this.listeners.add(listener);
		}
	}
	
	/** Unregisters a touch listener. If if was not registered, nothing. 
	 * @param listener 
	 * */
	public final void removeTouchListener(TouchListener listener){
		synchronized(this.listeners){
			this.listeners.remove(listener);
		}
	}
	
}