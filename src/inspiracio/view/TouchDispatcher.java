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

import android.util.Log;
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
 * <p>
 * The listeners you registers must execute fast, without executions,
 * in particular motion callbacks.
 * The listeners themselves must not add or remove listeners.
 * </p>
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
 * 	<li>onClick
 * 	<li><strike>onDoubleClick</strike>
 * 	<li>onDrag
 * 	<li><strike>onLongClick</strike>
 * 	<li>onZoom
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
	
	/** The last motion event that we have received.
	 * Maybe we can store less than the whole event. */
	private MotionEvent lastMotionEvent;
	
	//Constructor ---------------------------------------------
	
	public TouchDispatcher(){
		this.listeners=new ArrayList<TouchListener>();
		
		//Makes the multiplexer.
		//The client must promise not to register and unregister listeners in the callbacks.
		this.multiplexer=new TouchListener(){
			
			@Override public void onClick(MouseEvent e){
				for(TouchListener t : listeners)
					t.onClick(e);//I hope they are fast and none of them throws exception.
			}
			
			@Override public void onLongClick(MouseEvent e){throw new RuntimeException("not implemented");}
			@Override public void onDoubleClick(MouseEvent e){throw new RuntimeException("not implemented");}
			
			@Override public void onDrag(DragEvent e){
				for(TouchListener t : listeners)
					t.onDrag(e);//I hope they are fast and none of them throws exception.
			}
			
			@Override public void onZoom(ZoomEvent e){
				for(TouchListener t : listeners)
					t.onZoom(e);//I hope they are fast and none of them throws exception.
			}
			
			@Override public void onPressAndClick(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onPressAndDrag(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onRotate(EventObject e){throw new RuntimeException("not implemented");}
		};
	}
	
	//interface OnTouchListener -------------------------------
	
	private static enum Mode{
		/** No finger down. */
		M_0, 
		
		/** One finger down */
		M_1, 
		
		/** One finger moving */
		M_1_MOVING, 
		
		/** Two fingers down */
		M_2, 
		
		/** Two fingers moving */
		M_2_MOVING
	}
	private Mode mode=Mode.M_0;
	
	private static enum Gesture{NONE, CLICK, DRAG, ZOOM}
	
	/** What gesture is currently happening? */
	private Gesture gesture=Gesture.NONE;
	
	/** Distinguishes the events we implement and calls the listeners. */
	@Override public final boolean onTouch(View view, MotionEvent e){
		this.dumpEvent(mode, e);
		
		int action=e.getAction()&MotionEvent.ACTION_MASK;
		switch(action){
		
		//Cancel: forget everything
		case MotionEvent.ACTION_CANCEL:	//3
			mode=Mode.M_0;
			gesture=Gesture.NONE;
			break;
			
		//Setting down the first finger
		case MotionEvent.ACTION_DOWN:	//0
			mode=Mode.M_1;
			//Doesn't change current gesture. Could be CLICK, DRAG, or ZOOM.
			break;
			
		//Move: A drag or a zoom.
		case MotionEvent.ACTION_MOVE:	//2
			
			//A drag. One finger and no zoom.
			if((mode==Mode.M_1 || mode==Mode.M_1_MOVING) && (gesture==Gesture.NONE || gesture==Gesture.DRAG)){
				//For extra smoothness, report the historical points.				
				//The points, in chronological order.
				DragEvent de=new DragEvent(view);
				int size=e.getHistorySize();
				de.addPoint(this.lastMotionEvent.getX(), this.lastMotionEvent.getY());//The start point, from the last event.
				for(int i=0; i<size; i++)
					de.addPoint(e.getHistoricalX(i), e.getHistoricalY(i));//the historical points, from this event
				de.addPoint(e.getX(), e.getY());//The end point, from this event
				
				this.multiplexer.onDrag(de);
				mode=Mode.M_1_MOVING;
				gesture=Gesture.DRAG;
			}
			
			//A zoom. Two fingers and no drag.
			else if((mode==Mode.M_2 || mode==Mode.M_2_MOVING) && (gesture==Gesture.NONE || gesture==Gesture.ZOOM)){
				//For extra smoothness or some rotation, could add the historical points too. But now I'm doing just zoom, not rotation.
				//Pointer indexes may have changed since the last event.
				int p0id=this.lastMotionEvent.getPointerId(0);
				int p1id=this.lastMotionEvent.getPointerId(1);
				ZoomEvent pe=new ZoomEvent(view);
				pe.setInitialA(this.lastMotionEvent.getX(0), this.lastMotionEvent.getY(0));
				pe.setInitialB(this.lastMotionEvent.getX(1), this.lastMotionEvent.getY(1));
				int zero=e.findPointerIndex(p0id);
				int one=e.findPointerIndex(p1id);
				pe.setFinalA(e.getX(zero), e.getY(zero));
				pe.setFinalB(e.getX(one), e.getY(one));
				this.multiplexer.onZoom(pe);
				mode=Mode.M_2_MOVING;
				gesture=Gesture.ZOOM;
			}
			break;
			
		//Don't know what to do.
		case MotionEvent.ACTION_OUTSIDE:
			mode=Mode.M_0;
			break;
			
		//Another finger joins. Ignore third finger.
		case MotionEvent.ACTION_POINTER_DOWN:
			mode=Mode.M_2;
			break;
			
		//A finger leaves. Ignore third finger.
		case MotionEvent.ACTION_POINTER_UP:
			mode=Mode.M_1;
			break;
			
		//Raise last finger.
		case MotionEvent.ACTION_UP:	//1
			
			//A click
			if(gesture==Gesture.NONE && mode==Mode.M_1){
				MouseEvent me=new MouseEvent(view);
				me.set(e.getX(), e.getY());
				this.multiplexer.onClick(me);
			}
			
			mode=Mode.M_0;
			gesture=Gesture.NONE;
			break;
		}
				
		this.lastMotionEvent=MotionEvent.obtain(e);//In any case, remember the last motion event. This deep-copies the whole event.Maybe we only need some fields.
		return true;
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(Mode mode, MotionEvent event) {
	   String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
	      "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
	   StringBuilder sb = new StringBuilder();
	   int action = event.getAction();
	   int actionCode = action & MotionEvent.ACTION_MASK;
	   sb.append("event ACTION_" ).append(names[actionCode]);
	   if (actionCode == MotionEvent.ACTION_POINTER_DOWN
	         || actionCode == MotionEvent.ACTION_POINTER_UP) {
	      sb.append("(pid " ).append(
	      action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
	      sb.append(")" );
	   }
	   sb.append("[" );
	   for (int i = 0; i < event.getPointerCount(); i++) {
	      sb.append("#" ).append(i);
	      sb.append("(pid " ).append(event.getPointerId(i));
	      sb.append(")=" ).append((int) event.getX(i));
	      sb.append("," ).append((int) event.getY(i));
	      if (i + 1 < event.getPointerCount())
	         sb.append(";" );
	   }
	   sb.append("] ");
	   sb.append(mode.toString());
	   sb.append(" ");
	   sb.append(System.currentTimeMillis());
	   Log.d("TouchDispatcher", sb.toString());
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
	
	/** Unregisters a touch listener. If it was not registered, nothing. 
	 * @param listener 
	 * */
	public final void removeTouchListener(TouchListener listener){
		synchronized(this.listeners){
			this.listeners.remove(listener);
		}
	}
	
}